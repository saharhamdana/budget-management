package com.onetech.budget.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Historique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    @OneToOne
    private Categorie categorie;
    @ManyToOne
    private Client client;
    private boolean depassement;
    private Double valeurDepassement;

}