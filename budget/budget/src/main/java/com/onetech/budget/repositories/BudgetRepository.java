package com.onetech.budget.repositories;

import com.onetech.budget.models.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserId(String userId);

}