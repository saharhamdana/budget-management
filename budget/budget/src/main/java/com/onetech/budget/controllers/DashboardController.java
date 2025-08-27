package com.onetech.budget.controllers;

import com.onetech.budget.models.Budget;
import com.onetech.budget.repositories.BudgetRepository;
import com.onetech.budget.repositories.TransactionRepository;
import com.onetech.budget.services.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getSubject();

        Map<String, Object> response = new HashMap<>();

        double totalSpent = dashboardService.sumTransactionsByUser(userId);
        Map<String, Double> byCategorie = dashboardService.sumByCategorieAndUser(userId);
        List<Map<String, Object>> budgetVsReal = dashboardService.compareBudgetVsReal(userId);

        double totalBudget = dashboardService.sumBudgetsByUser(userId);

        response.put("totalTransactions", totalSpent);
        response.put("byCategorie", byCategorie);
        response.put("budgetVsReal", budgetVsReal);
        response.put("totalBudget", totalBudget);
        response.put("totalSpent", totalSpent);

        return ResponseEntity.ok(response);
    }

}


