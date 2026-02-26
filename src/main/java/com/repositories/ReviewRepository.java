package com.repositories;
import com.entities.Review;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviewRepository {
    private final MongoCollection<Document> collection;

    public ReviewRepository() {
        // En local, MongoDB tourne sur le port 27017
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("vod_db");
        this.collection = database.getCollection("reviews");
    }

    public void save(Review review) {
        Document doc = new Document("titreFilm", review.getTitreFilm())
                .append("pseudo", review.getPseudo())
                .append("note", review.getNote())
                .append("commentaire", review.getCommentaire());
        collection.insertOne(doc);
    }

    public List<Document> findByFilm(String titreFilm) {
        return collection.find(Filters.eq("titreFilm", titreFilm)).into(new ArrayList<>());
    }

    public double getAverage(String titreFilm) {
        List<Bson> pipeline = Arrays.asList(
                Aggregates.match(Filters.eq("titreFilm", titreFilm)),
                Aggregates.group(null, Accumulators.avg("avgNote", "$note"))
        );
        Document result = collection.aggregate(pipeline).first();
        return (result != null && result.get("avgNote") != null) ? result.getDouble("avgNote") : 0.0;
    }
}
