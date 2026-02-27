package com.repositories;

import com.entities.Review;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repository pour les évaluations stockées dans MongoDB.
 * Collection : "reviews"
 */
public class ReviewRepository {

    private final MongoCollection<Document> collection;

    public ReviewRepository() {
        // URL configurable via variable d'environnement (pour Docker)
        // En local : mongodb://localhost:27017
        // En Docker : mongodb://mongodb:27017
        String mongoUrl = System.getenv("MONGODB_URI");
        if (mongoUrl == null || mongoUrl.isBlank()) {
            mongoUrl = "mongodb://localhost:27017";
        }

        MongoClient mongoClient = MongoClients.create(mongoUrl);
        MongoDatabase database = mongoClient.getDatabase("vod_db");
        this.collection = database.getCollection("reviews");
    }

    /**
     * Sauvegarde une évaluation.
     */
    public void save(Review review) {
        Document doc = new Document("titreFilm", review.getTitreFilm())
                .append("pseudo", review.getPseudo())
                .append("note", review.getNote())
                .append("commentaire", review.getCommentaire());
        collection.insertOne(doc);
    }

    /**
     * Récupère toutes les évaluations d'un film.
     */
    public List<Document> findByFilm(String titreFilm) {
        return collection.find(Filters.eq("titreFilm", titreFilm))
                .into(new ArrayList<>());
    }

    /**
     * Calcule la moyenne des notes d'un film.
     */
    public double getAverage(String titreFilm) {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(Filters.eq("titreFilm", titreFilm)),
                Aggregates.group(null, Accumulators.avg("avgNote", "$note"))
        );
        Document result = collection.aggregate(pipeline).first();
        return (result != null && result.get("avgNote") != null)
                ? result.getDouble("avgNote")
                : 0.0;
    }

    /**
     * Compte le nombre d'évaluations d'un film.
     */
    public long countByFilm(String titreFilm) {
        return collection.countDocuments(Filters.eq("titreFilm", titreFilm));
    }

    /**
     * Vérifie si un utilisateur a déjà évalué un film.
     * Contrainte : un utilisateur ne peut évaluer qu'une seule fois par film.
     */
    public boolean existsByFilmAndPseudo(String titreFilm, String pseudo) {
        Document result = collection.find(
                Filters.and(
                        Filters.eq("titreFilm", titreFilm),
                        Filters.eq("pseudo", pseudo)
                )
        ).first();
        return result != null;
    }
}