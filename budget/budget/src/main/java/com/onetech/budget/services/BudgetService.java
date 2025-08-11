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
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategorieRepository categorieRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public BudgetService(BudgetRepository budgetRepository, CategorieRepository categorieRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.categorieRepository = categorieRepository;
        this.transactionRepository = transactionRepository;
    }

    public Budget saveBudget(Budget budget) {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        budget.setUserId(userId);

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

        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        if (!existingBudget.getUserId().equals(userId)) {
            throw new RuntimeException("Non autorisé à modifier ce budget");
        }

        existingBudget.setAmountPerMonth(updatedBudget.getAmountPerMonth());
        existingBudget.setRealAmount(updatedBudget.getRealAmount());
        existingBudget.setCategorie(updatedBudget.getCategorie());

        calculerDepassement(existingBudget);

        return budgetRepository.save(existingBudget);
    }

    public List<Budget> getBudgetsForCurrentUser() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();
        return budgetRepository.findByUserId(userId);
    }

    public Budget findById(Long id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        budgetRepository.deleteById(id);
    }

    private void calculerDepassement(Budget budget) {
        Double amountPerMonth = budget.getAmountPerMonth();
        Double realAmount = budget.getRealAmount();

        if (realAmount != null && amountPerMonth != null) {
            if (realAmount > amountPerMonth) {
                budget.setDepassement(true);
                budget.setValeurDepassement(realAmount - amountPerMonth);
            } else {
                budget.setDepassement(false);
                budget.setValeurDepassement(0.0);
            }
        }
    }
    public Budget updateRealAmountFromTransactions(Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget non trouvé"));

        // Supposons que tu as une méthode pour récupérer les transactions par catégorie
        List<Transaction> transactions = transactionRepository.findByCategorie(budget.getCategorie());

        double sumTransactions = transactions.stream()
                .mapToDouble(Transaction::getMontant)
                .sum();

        budget.setRealAmount(sumTransactions);

        if (sumTransactions > budget.getAmountPerMonth()) {
            budget.setDepassement(true);
            budget.setValeurDepassement(sumTransactions - budget.getAmountPerMonth());
        } else {
            budget.setDepassement(false);
            budget.setValeurDepassement(0.0);
        }

        return budgetRepository.save(budget);
    }



}
