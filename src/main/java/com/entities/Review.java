package com.entities;

public class Review {
    private String titreFilm;
    private String pseudo;
    private int note; // Doit être entre 0 et 5
    private String commentaire;

    public Review() {}

    public Review(String titreFilm, String pseudo, int note, String commentaire) {
        this.titreFilm = titreFilm;
        this.pseudo = pseudo;
        this.note = note;
        this.commentaire = commentaire;
    }

    // Getters et Setters nécessaires pour GSON
    public String getTitreFilm() { return titreFilm; }
    public void setTitreFilm(String titreFilm) {this.titreFilm = titreFilm;}
    public String getPseudo() { return pseudo; }
    public int getNote() { return note; }
    public String getCommentaire() { return commentaire; }
}
