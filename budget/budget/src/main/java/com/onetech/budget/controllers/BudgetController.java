package com.onetech.budget.controllers;

import com.onetech.budget.DTO.RealAmountDTO;
import com.onetech.budget.models.Budget;

import com.onetech.budget.repositories.BudgetRepository;
import com.onetech.budget.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;
    private final BudgetRepository budgetRepository;

    @Autowired
    public BudgetController(BudgetService budgetService,  BudgetRepository budgetRepository) {
        this.budgetService = budgetService;
        this.budgetRepository = budgetRepository;
    }

    @PostMapping
    public ResponseEntity<?> createBudget(@RequestBody Budget budget) {
        try {
            Budget saved = budgetService.saveBudget(budget);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            // Log dans la console
            e.printStackTrace();

            // Retourner un message d'erreur au client
            return ResponseEntity.status(500).body("Erreur lors de la création du budget : " + e.getMessage());
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budget) {
        Budget updated = budgetService.updateBudget(id, budget);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public List<Budget> getUserBudgets() {
        return budgetService.getBudgetsForCurrentUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        Budget budget = budgetService.findById(id);
        if (budget == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(budget);
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id) {
        budgetService.deleteById(id);
    }

    @PutMapping("/{id}/realAmount/updateFromTransactions")
    public ResponseEntity<Budget> updateRealAmountFromTransactions(@PathVariable Long id) {
        Budget updatedBudget = budgetService.updateRealAmountFromTransactions(id);
        return ResponseEntity.ok(updatedBudget);
    }


}
