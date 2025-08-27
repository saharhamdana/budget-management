package com.onetech.budget.services;

import com.onetech.budget.models.Budget;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.repositories.BudgetRepository;
import com.onetech.budget.repositories.TransactionRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    public DashboardService(TransactionRepository transactionRepository,
                            BudgetRepository budgetRepository) {
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
    }

    // Total transactions d'un utilisateur
    public double sumTransactionsByUser(String client) {
        return transactionRepository.findByClient(client).stream()
                .mapToDouble(Transaction::getMontant)
                .sum();
    }

    // Total transactions groupées par catégorie
    public Map<String, Double> sumByCategorieAndUser(String client) {
        return transactionRepository.findByClient(client).stream()
                .collect(Collectors.groupingBy(
                        t -> t.getCategorie() != null ? t.getCategorie().getNomAng() : "Sans catégorie",
                        Collectors.summingDouble(Transaction::getMontant)
                ));
    }

    // Comparaison budgets / dépenses réelles par catégorie
    public List<Map<String, Object>> compareBudgetVsReal(String userId) {
        List<Map<String, Object>> result = new ArrayList<>();

        List<Budget> budgets = budgetRepository.findByUserId(userId);

        for (Budget budget : budgets) {
            Map<String, Object> data = new HashMap<>();
            double realSpent = transactionRepository.findByCategorieAndClient(budget.getCategorie(), userId)
                    .stream()
                    .mapToDouble(Transaction::getMontant)
                    .sum();

            data.put("categorie", budget.getCategorie().getNomAng());
            data.put("budget", budget.getAmountPerMonth());
            data.put("realSpent", realSpent);
            data.put("depassement", realSpent > budget.getAmountPerMonth());

            result.add(data);
        }

        return result;
    }


    public double sumBudgetsByUser(String userId) {
        return budgetRepository.findByUserId(userId).stream()
                .mapToDouble(b -> b.getAmountPerMonth() != null ? b.getAmountPerMonth() : 0.0)
                .sum();
    }
}
