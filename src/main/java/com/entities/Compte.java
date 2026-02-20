package com.entities;

import com.enums.RoleCompte;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

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
    private String motDePasse;

    /**
     * Calcule l'âge à partir de dateDeNaissance (format "YYYY-MM-DD").
     * Contrainte UML : 0 < age < 150
     */
    public int getAge() {
      if (dateDeNaissance == null || dateDeNaissance.isBlank()) return 0;
      LocalDate naissance = LocalDate.parse(dateDeNaissance);
      return Period.between(naissance, LocalDate.now()).getYears();
    }

}
