package com.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class Location {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "compte_id")
    private Compte compte;

    @ManyToOne
    @JoinColumn(name = "film_id")
    private Film film;

    private LocalDate dateDebut;
    private LocalDate dateFin;

}
