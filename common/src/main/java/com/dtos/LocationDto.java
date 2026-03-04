package com.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LocationDto {

    private Long Id;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String pseudo;

    private Long filmId;

    private String titreFilm;

    private boolean enCours;

}
