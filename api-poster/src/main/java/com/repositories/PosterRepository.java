package com.repositories;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class PosterRepository {
    private final MongoCollection<Document> collection;

    public PosterRepository() {
        // URL configurable via variable d'environnement (pour Docker)
        // En local : mongodb://localhost:27017
        // En Docker : mongodb://mongodb:27017
        String mongoUrl = System.getenv("MONGODB_URI");
        if (mongoUrl == null || mongoUrl.isBlank()) {
            mongoUrl = "mongodb://poster-mongodb:27017";
        }

        MongoClient mongoClient = MongoClients.create(mongoUrl);
        MongoDatabase database = mongoClient.getDatabase("mps_db");
        this.collection = database.getCollection("posters");
    }

    public String save(String titreFilm, String nom) {
        Document doc = new Document("titreFilm", titreFilm)
                .append("nom", nom);
        collection.insertOne(doc);
        return doc.getObjectId("_id").toHexString();
    }

    public List<Document> findByFilm(String titreFilm) {
        List<Document> docs = collection.find(Filters.eq("titreFilm", titreFilm)).into(new ArrayList<>());
        for (Document doc : docs) {
            doc.put("_id", doc.getObjectId("_id").toHexString());
        }
        return docs;
    }

    public Document findById(String id) {
        return collection.find(Filters.eq("_id", new ObjectId(id))).first();
    }

    public void update(String id, String nom) {
        collection.updateOne(Filters.eq("_id", new ObjectId(id)),
                new Document("$set", new Document("nom", nom)));
    }

    public void delete(String id) {
        collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
    }
}