package com.onetech.budget.controllers;

import com.onetech.budget.models.Budget;
import com.onetech.budget.services.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping
    public Budget create(@RequestBody Budget budget) {
        return budgetService.save(budget);
    }

    @GetMapping
    public List<Budget> getAll() {
        return budgetService.getAll();
    }

    @GetMapping("/{id}")
    public Budget getById(@PathVariable Long id) {
        return budgetService.getById(id);
    }

    @PutMapping("/{id}")
    public Budget update(@PathVariable Long id, @RequestBody Budget budget) {
        return budgetService.update(id, budget);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        budgetService.delete(id);
    }
}