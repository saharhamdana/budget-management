package com.onetech.budget.services;


import com.onetech.budget.models.Budget;
import com.onetech.budget.repositories.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> getAll() {
        return budgetRepository.findAll();
    }

    public Budget getById(Long id) {
        return budgetRepository.findById(id).orElse(null);
    }

    public Budget update(Long id, Budget updated) {
        Budget existing = budgetRepository.findById(id).orElse(null);
        if (existing != null) {
            existing.setMontant(updated.getMontant());
            existing.setPeriode(updated.getPeriode());
            existing.setCategorie(updated.getCategorie());
            existing.setClient(updated.getClient());
            return budgetRepository.save(existing);
        }
        return null;
    }

    public void delete(Long id) {
        budgetRepository.deleteById(id);
    }
}