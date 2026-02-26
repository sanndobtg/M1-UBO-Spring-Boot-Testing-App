package com.controllers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Gestion globale des exceptions pour tous les controllers.
 * L'idee c'est de l'ecrire une fois,
 * pour fonctionner pour toute l'application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * EntityNotFoundException → 404 Not Found
     * (film non trouvé, compte non trouvé, location non trouvée)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(EntityNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    /**
     * IllegalStateException → 403 Forbidden
     * (quota atteint, âge insuffisant, film non louable)
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbidden(IllegalStateException e) {
        return Map.of("error", e.getMessage());
    }

}