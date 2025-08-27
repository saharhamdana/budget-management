package com.onetech.budget.DTO;

import com.onetech.budget.models.Categorie;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BudgetStatus {
    private Categorie category;
    private Double allocated;
    private Double spent;
    private Boolean exceeded;
}
