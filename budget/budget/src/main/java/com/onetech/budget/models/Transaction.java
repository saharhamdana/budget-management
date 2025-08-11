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
    private String description;
    @Column(unique = true, nullable = false)
    private String reference;
   // @ManyToOne
    private String client;

    @ManyToOne
    private  Categorie categorie;


    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }
}