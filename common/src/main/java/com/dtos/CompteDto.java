package com.dtos;


import com.enums.RoleCompte;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompteDto {


    private Long Id;


    private RoleCompte role;


    private String pseudo;


    private String nom;


    private String prenom;


    private String dateDeNaissance;


    private String adresse;

    private String motDePasse;
}