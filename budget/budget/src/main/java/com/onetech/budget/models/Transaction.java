package com.onetech.budget.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double montant;
    private LocalDate dateOpertation;
    private LocalDate dateValeur;
    @ManyToOne
    private Client client;

    // private  Categorie categorie;


}