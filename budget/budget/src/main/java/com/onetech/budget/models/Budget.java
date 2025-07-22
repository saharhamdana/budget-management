package com.onetech.budget.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amountPerMonth;

    @OneToOne
    //@JoinColumn(name = "categorie_id")
    private Categorie categorie;


    private String userId;
    private Double realAmount;

    @Column(name = "depassement")
    private Boolean depassement;

    @Column(name = "valeur_depassement")
    private Double valeurDepassement;

}