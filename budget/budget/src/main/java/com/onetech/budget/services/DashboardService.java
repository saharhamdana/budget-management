package com.onetech.budget.services;

import com.onetech.budget.models.Budget;
import com.onetech.budget.models.Transaction;
import com.onetech.budget.repositories.BudgetRepository;
import com.onetech.budget.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DashboardService(BudgetRepository budgetRepository,
                            TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    public Map<String, Object> getDashboardData(String userId) {
        Map<String, Object> data = new HashMap<>();

        List<Budget> budgets = budgetRepository.findByUserId(userId);
        List<Transaction> transactions = transactionRepository.findByClient(userId);

        double totalBudget = budgets.stream().mapToDouble(Budget::getAmountPerMonth).sum();
        double totalSpent = transactions.stream().mapToDouble(Transaction::getMontant).sum();

        data.put("totalBudget", totalBudget);
        data.put("totalSpent", totalSpent);
        data.put("remaining", totalBudget - totalSpent);
        data.put("budgets", budgets);
        data.put("transactions", transactions);

        return data;
    }
}
