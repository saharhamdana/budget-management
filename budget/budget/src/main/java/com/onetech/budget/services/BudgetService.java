package com.onetech.budget.services;

import com.onetech.budget.models.Budget;
import com.onetech.budget.models.Categorie;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.repositories.BudgetRepository;
import com.onetech.budget.repositories.CategorieRepository;
import com.onetech.budget.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategorieRepository categorieRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository,
                         CategorieRepository categorieRepository,
                         TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categorieRepository = categorieRepository;
        this.transactionRepository = transactionRepository;
    }

    // --- Méthodes utilitaires pour JWT ---
    private String getCurrentUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }

    private String getCurrentUserEmail() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaimAsString("email");
    }

    // --- CRUD Budgets ---
    public Budget saveBudget(Budget budget) {
        budget.setUserId(getCurrentUserId());

        if (budget.getCategorie() == null || budget.getCategorie().getId() == null) {
            throw new RuntimeException("Catégorie obligatoire");
        }

        Categorie cat = categorieRepository.findById(budget.getCategorie().getId())
                .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
        budget.setCategorie(cat);

        calculerDepassement(budget);

        return budgetRepository.save(budget);
    }

    public Budget updateBudget(Long id, Budget updatedBudget) {
        Budget existingBudget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget introuvable"));

        if (!existingBudget.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("Non autorisé à modifier ce budget");
        }

        existingBudget.setAmountPerMonth(updatedBudget.getAmountPerMonth());
        existingBudget.setRealAmount(updatedBudget.getRealAmount());

        if (updatedBudget.getCategorie() != null && updatedBudget.getCategorie().getId() != null) {
            Categorie cat = categorieRepository.findById(updatedBudget.getCategorie().getId())
                    .orElseThrow(() -> new RuntimeException("Catégorie introuvable"));
            existingBudget.setCategorie(cat);
        }

        calculerDepassement(existingBudget);

        return budgetRepository.save(existingBudget);
    }

    public List<Budget> getBudgetsForCurrentUser() {
        return budgetRepository.findByUserId(getCurrentUserId());
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget introuvable"));

        if (!budget.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("Non autorisé à supprimer ce budget");
        }

        budgetRepository.deleteById(id);
    }

    // --- Calcul du dépassement et envoi de mail ---
    private void calculerDepassement(Budget budget) {
        Double amountPerMonth = budget.getAmountPerMonth();
        Double realAmount = budget.getRealAmount();

        if (realAmount != null && amountPerMonth != null) {
            if (realAmount > amountPerMonth) {
                budget.setDepassement(true);
                budget.setValeurDepassement(realAmount - amountPerMonth);

                String userEmail = getCurrentUserEmail();
                if (userEmail != null) {
                    emailService.sendEmail(
                            userEmail,
                            "Dépassement de budget",
                            "Attention ! Vous avez dépassé votre budget pour la catégorie " +
                                    budget.getCategorie().getNomAng() + ". Montant dépassé : " +
                                    budget.getValeurDepassement() + " €"
                    );
                }
            } else {
                budget.setDepassement(false);
                budget.setValeurDepassement(0.0);
            }
        }
    }

    public Budget updateRealAmountFromTransactions(Long budgetId) {
        try {
            // 🔑 Récupération du JWT
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (!(principal instanceof Jwt jwt)) {
                throw new RuntimeException("Utilisateur non authentifié ou JWT manquant");
            }

            String userId = jwt.getSubject();
            if (userId == null) {
                throw new RuntimeException("Impossible de récupérer l'ID utilisateur depuis le JWT");
            }

            // 🔎 Récupération du budget
            Budget budget = budgetRepository.findById(budgetId)
                    .orElseThrow(() -> new RuntimeException("Budget non trouvé pour l'ID: " + budgetId));

            if (budget.getCategorie() == null) {
                throw new RuntimeException("Le budget n'a pas de catégorie associée");
            }

            // 💰 Récupération des transactions liées à la catégorie + utilisateur
            List<Transaction> transactions = transactionRepository.findByCategorieAndClient(budget.getCategorie(), userId);
            if (transactions == null) {
                transactions = List.of(); // Sécurité
            }

            double sumTransactions = transactions.stream()
                    .mapToDouble(Transaction::getMontant)
                    .sum();

            // Mise à jour du montant réel
            budget.setRealAmount(sumTransactions);

            // Sauvegarde de l’état précédent
            boolean wasAlreadyExceeded = Boolean.TRUE.equals(budget.getDepassement());

            // 📊 Vérification dépassement
            if (budget.getAmountPerMonth() != null && sumTransactions > budget.getAmountPerMonth()) {
                budget.setDepassement(true);
                budget.setValeurDepassement(sumTransactions - budget.getAmountPerMonth());

                // 📧 Envoi du mail uniquement si NOUVEAU dépassement
                String userEmail = jwt.getClaimAsString("email");
                if (!wasAlreadyExceeded && userEmail != null && !userEmail.isBlank()) {
                    try {
                        emailService.sendEmail(
                                userEmail,
                                "Budget Exceeded",
                                "Warning! You have exceeded your budget for the category " +
                                        budget.getCategorie().getNomAng() +
                                        ". Exceeded amount: " + budget.getValeurDepassement() + " €"
                        );
                        System.out.println("📧 Mail envoyé à " + userEmail + " pour dépassement de budget.");
                    } catch (Exception e) {
                        System.err.println("❌ Error while sending the email: " + e.getMessage());
                    }
                }
            } else {
                budget.setDepassement(false);
                budget.setValeurDepassement(0.0);
            }

            // Sauvegarde en BDD
            Budget updatedBudget = budgetRepository.save(budget);
            System.out.println("✅ Budget mis à jour : " + updatedBudget);

            return updatedBudget;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du montant réel du budget : " + e.getMessage());
        }
    }



}
