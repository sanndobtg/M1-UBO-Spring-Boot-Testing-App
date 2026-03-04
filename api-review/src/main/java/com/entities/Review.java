package com.entities;

/**
 * Entité représentant une évaluation de film.
 * Stockée dans MongoDB (collection "reviews").
 *
 * Contraintes UML :
 * - 0 < note < 5 (strictement)
 * - Evaluation possible seulement si la location est payée
 * - Un utilisateur ne peut évaluer un film qu'une seule fois
 */
public class Review {

    private String titreFilm;
    private String pseudo;
    private int note;
    private String commentaire;

    public Review() {}

    public Review(String titreFilm, String pseudo, int note, String commentaire) {
        this.titreFilm = titreFilm;
        this.pseudo = pseudo;
        this.note = note;
        this.commentaire = commentaire;
    }

    public String getTitreFilm() { return titreFilm; }
    public void setTitreFilm(String titreFilm) { this.titreFilm = titreFilm; }

    public String getPseudo() { return pseudo; }
    public void setPseudo(String pseudo) { this.pseudo = pseudo; }

    public int getNote() { return note; }
    public void setNote(int note) { this.note = note; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}