package com.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LocationDto {

    @NotNull
    private Long Id;

    private String pseudo;
    private Long filmId;
    private String titreFilm;
    private boolean enCours;
    @NotBlank(message = "La Date du debut est obligatoire")
    private LocalDate dateDebut;

    @NotBlank(message = "La date de fin est obligatoire")
    private LocalDate dateFin;


}
