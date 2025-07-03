package com.onetech.budget.DTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequestDTO {
    private Double montant;
    private LocalDate dateOpertation;
    private LocalDate dateValeur;
    private Long clientId;
}