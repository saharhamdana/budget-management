package com.onetech.budget.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomAng;
    private String nomAr;


}