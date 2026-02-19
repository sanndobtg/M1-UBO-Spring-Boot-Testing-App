package com.entities;

import com.enums.RoleCompte;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private RoleCompte role;
    private String pseudo;
    private String nom;
    private String prenom;
    private String dateDeNaissance;
    private String adresse;
    private String mdp;
}
