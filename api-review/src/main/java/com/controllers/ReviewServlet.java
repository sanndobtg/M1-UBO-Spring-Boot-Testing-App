package com.controllers;

import com.entities.Review;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.repositories.ReviewRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

/**
 * Servlet gérant les évaluations de films.
 * Technologie : Servlet (comme demandé dans le cahier des charges).
 * Stockage : MongoDB.
 *
 * Endpoints :
 * - GET /reviews/{titreFilm} : consulter les avis (sans connexion)
 * - GET /reviews/{titreFilm}/average : moyenne des notes (sans connexion)
 * - POST /reviews/{titreFilm} : ajouter un avis (connexion + location requise)
 */
@WebServlet("/reviews/*")
public class ReviewServlet extends HttpServlet {

    private final ReviewRepository repo = new ReviewRepository();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "Titre du film manquant");
            return;
        }

        String[] parts = pathInfo.split("/");
        String titreFilm = parts[1];

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        if (parts.length > 2 && parts[2].equals("average")) {
            // GET /reviews/{titreFilm}/average — Moyenne des évaluations
            double avg = repo.getAverage(titreFilm);
            long count = repo.countByFilm(titreFilm);
            resp.getWriter().print(mapper.writeValueAsString(
                    new AverageResponse(titreFilm, avg, count)));
        } else {
            // GET /reviews/{titreFilm} — Liste des évaluations (accessible sans connexion)
            resp.getWriter().print(mapper.writeValueAsString(repo.findByFilm(titreFilm)));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. Vérifier que l'utilisateur est connecté (header X-User-Pseudo)
        String pseudo = req.getHeader("X-User-Pseudo");
        if (pseudo == null || pseudo.isBlank()) {
            resp.sendError(401, "Vous devez être connecté pour évaluer un film");
            return;
        }

        // 2. Extraire le titre du film depuis l'URL
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "Titre du film manquant dans l'URL");
            return;
        }
        String titreFilm = pathInfo.split("/")[1];

        // 3. Lire le JSON envoyé par le client
        Review review = mapper.readValue(req.getReader(), Review.class);

        // 4. Injecter le pseudo et le titre du film
        review.setTitreFilm(titreFilm);
        review.setPseudo(pseudo);

        // 5. Validation métier : note strictement entre 0 et 5
        // Contrainte UML : "0 < evaluation < 5"
        if (review.getNote() <= 0 || review.getNote() >= 5) {
            resp.sendError(400, "La note doit être strictement entre 0 et 5 (1, 2, 3 ou 4)");
            return;
        }

        // 6. Vérifier que l'utilisateur n'a pas déjà évalué ce film
        if (repo.existsByFilmAndPseudo(titreFilm, pseudo)) {
            resp.sendError(403, "Vous avez déjà évalué ce film");
            return;
        }

        // 7. Sauvegarde
        repo.save(review);

        resp.setStatus(201);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print(mapper.writeValueAsString(review));

    }

    /**
     * Classe interne pour la réponse de la moyenne.
     */
    private static class AverageResponse {

        public String titreFilm;
        public double moyenne;
        public long nbEvaluations;

        AverageResponse(String titreFilm, double moyenne, long nbEvaluations) {
            this.titreFilm = titreFilm;
            this.moyenne = moyenne;
            this.nbEvaluations = nbEvaluations;
        }

    }

}