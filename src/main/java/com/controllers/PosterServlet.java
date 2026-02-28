package com.controllers;

import com.entities.Poster;
import com.google.gson.Gson;
import com.repositories.PosterRepository;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/posters/*")
public class PosterServlet extends HttpServlet {
    private final PosterRepository repo = new PosterRepository();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /{titreFilm} ou /{id}
        resp.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "Titre du film manquant dans l'URL");
            return;
        }

        String key = pathInfo.substring(1);
        if (key.length() == 24) {
            // GET par id
            var doc = repo.findById(key);
            if (doc == null) {
                resp.sendError(404, "Poster non trouvé");
            } else {
                doc.put("_id", doc.getObjectId("_id").toHexString());
                resp.getWriter().print(gson.toJson(doc));
            }
        } else {
            // GET par titreFilm
            List docs = repo.findByFilm(key);
            resp.getWriter().print(gson.toJson(docs));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /{titreFilm}
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "Titre du film manquant dans l'URL");
            return;
        }

        String titreFilm = pathInfo.substring(1);
        Poster poster = gson.fromJson(req.getReader(), Poster.class);
        if (poster.getNom() == null || poster.getNom().isEmpty()) {
            resp.sendError(400, "Le lien du poster est obligatoire");
            return;
        }

        String id = repo.save(titreFilm, poster.getNom());
        resp.setContentType("application/json");
        resp.getWriter().print("{\"id\":\"" + id + "\"}");
        resp.setStatus(201);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /{posterId}
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "ID du poster manquant");
            return;
        }

        String posterId = pathInfo.substring(1);
        if (posterId.length() != 24 || !posterId.matches("[0-9a-fA-F]+")) {
            resp.sendError(400, "ID du poster invalide : doit être 24 caractères hexadécimaux");
            return;
        }

        Poster poster = gson.fromJson(req.getReader(), Poster.class);
        if (poster.getNom() == null || poster.getNom().isEmpty()) {
            resp.sendError(400, "Le lien du poster est obligatoire");
            return;
        }

        if (repo.findById(posterId) == null) {
            resp.sendError(404, "Poster non trouvé");
            return;
        }

        repo.update(posterId, poster.getNom());
        resp.setStatus(200);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo(); // /{posterId}
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.sendError(400, "ID du poster manquant");
            return;
        }

        String posterId = pathInfo.substring(1);

        if (posterId.length() != 24 || !posterId.matches("[0-9a-fA-F]+")) {
            resp.sendError(400, "ID du poster invalide : doit être 24 caractères hexadécimaux");
            return;
        }

        if (repo.findById(posterId) == null) {
            resp.sendError(404, "Poster non trouvé");
            return;
        }

        repo.delete(posterId);
        resp.setStatus(204); // No Content
    }
}
