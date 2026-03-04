package com.entities;

public class Poster {
    private String id;      // hex string Mongo
    private String titreFilm;
    private String nom;     // URL du poster

    public Poster() {}

    public Poster(String id, String titreFilm, String nom) {
        this.id = id;
        this.titreFilm = titreFilm;
        this.nom = nom;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitreFilm() { return titreFilm; }
    public void setTitreFilm(String titreFilm) { this.titreFilm = titreFilm; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}