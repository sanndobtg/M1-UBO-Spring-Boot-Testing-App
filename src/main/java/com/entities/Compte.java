package com.entities;

import com.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Compte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private Role role;
    private String pseudo;
    private String nom;
    private String prenom;
    private String dateDeNaissance;
    private String adresse;
    private String mdp;


}
