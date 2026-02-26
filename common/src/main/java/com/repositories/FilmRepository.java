package com.repositories;

import com.entities.Film;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long> {

    /**
     * Recherche un film par son titre (unique).
     * Utilisé par LocationServiceImpl pour trouver le film à louer/rendre.
     * Génère : SELECT * FROM film WHERE titre = ?
     */
    Optional<Film> findByTitre(String titre);
}
