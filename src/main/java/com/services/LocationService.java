package com.services;

import com.dtos.LocationDto;

import java.util.List;

/**
 * Interface définissant les opérations métier disponibles pour la gestion des locations de films.
 * Cette interface suit le principe d'Interface Segregation (SOLID).
 */
public interface LocationService {

    /**
     * Réserve un film pour un utilisateur.
     * Vérifie : film louable, max 3 locations en cours, âge >= âge minimum du film.
     * @param pseudo le pseudo de l'utilisateur connecté
     * @param titreFilm le titre du film à réserver
     * @return la location créée avec dateRendu null (en cours)
     * @throws RuntimeException si le film n'existe pas ou le compte n'existe pas
     * @throws IllegalStateException si le quota est atteint, l'âge insuffisant ou le film non louable
     */
    LocationDto reserverFilm(String pseudo, String titreFilm);

    /**
     * Termine la location d'un film pour un utilisateur.
     * Met la dateRendu à maintenant.
     * @param pseudo le pseudo de l'utilisateur connecté
     * @param titreFilm le titre du film à rendre
     * @return la location mise à jour avec dateRendu renseignée
     * @throws RuntimeException si le film, le compte ou la location en cours n'existe pas
     */
    LocationDto terminerLocation(String pseudo, String titreFilm);

    /**
     * Récupère les films actuellement en location par un utilisateur (dateRendu == null).
     * @param pseudo le pseudo de l'utilisateur connecté
     * @return la liste des locations en cours
     * @throws RuntimeException si le compte n'existe pas
     */
    List<LocationDto> getLocationsEnCours(String pseudo);
}
