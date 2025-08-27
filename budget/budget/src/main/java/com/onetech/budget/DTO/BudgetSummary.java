package com.onetech.budget.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BudgetSummary {
    private Integer totalBudgets;
    private Integer exceededBudgets;
    private Double totalAllocated;
    private List<BudgetStatus> budgetStatuses;
}