package com.onetech.budget.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetRequestDTO {
    private BigDecimal montant;
    private String periode; // Format attendu : "2025-07"
    private Long categorieId;
    private Long clientId;
}
