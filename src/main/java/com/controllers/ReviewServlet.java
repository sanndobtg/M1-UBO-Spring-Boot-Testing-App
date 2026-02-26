package com.controllers;

import com.entities.Review;
import com.google.gson.Gson;
import com.repositories.ReviewRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/reviews/*")
public class ReviewServlet extends HttpServlet {
    private final ReviewRepository repo = new ReviewRepository();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "Titre du film manquant");
            return;
        }

        String titreFilm = pathInfo.split("/")[1];
        resp.setContentType("application/json");

        if (pathInfo.contains("/average")) {
            // Moyenne des évaluations [cite: 21]
            double avg = repo.getAverage(titreFilm);
            resp.getWriter().print("{\"titreFilm\":\"" + titreFilm + "\", \"moyenne\":" + avg + "}");
        } else {
            // Liste des évaluations [cite: 20]
            resp.getWriter().print(gson.toJson(repo.findByFilm(titreFilm)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1. Extraire le titre du film depuis l'URL (comme on le fait dans le GET)
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "Titre du film manquant dans l'URL");
            return;
        }
        String titreFilm = pathInfo.split("/")[1];

        // 2. Lire le JSON envoyé par le client
        Review review = gson.fromJson(req.getReader(), Review.class);

        // 3. INJECTION : On force le titre du film dans l'objet
        review.setTitreFilm(titreFilm);

        // 4. Validation métier
        if (review.getNote() < 0 || review.getNote() > 5) {
            resp.sendError(400, "La note doit être entre 0 et 5");
            return;
        }

        // 5. Sauvegarde
        repo.save(review);
        resp.setStatus(201);
    }
}
