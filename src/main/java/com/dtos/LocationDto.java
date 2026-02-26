package com.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LocationDto {

    @NotNull
    private Long Id;

    @NotBlank(message = "La Date du debut est obligatoire")
    private LocalDate dateDebut;

    @NotBlank(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotBlank(message = "La pseudo est obligatoire")
    private String pseudo;

    @NotBlank(message = "La filmId est obligatoire")
    private Long filmId;

    @NotBlank(message = "La titreFilm est obligatoire")
    private String titreFilm;

    @NotBlank(message = "Ce film est-il en cours de location?")
    private boolean enCours;

}
