package com.repositories;

import com.entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    /**
     * Locations en cours d'un compte (dateFin == null).
     * Utilisé par getLocationsEnCours().
     * Génère : SELECT * FROM location WHERE compte_id = ? AND date_fin IS NULL
     */
    List<Location> findByCompteIdAndDateFinIsNull(Long compteId);

    /**
     * Compte le nombre de locations en cours (max 3).
     * Utilisé par reserverFilm() pour vérifier le quota.
     * Génère : SELECT COUNT(*) FROM location WHERE compte_id = ? AND date_fin IS NULL
     */
    Long countByCompteIdAndDateFinIsNull(Long compteId);

    /**
     * Trouve la location en cours d'un compte pour un film donné.
     * Utilisé par terminerLocation().
     */
    @Query("SELECT l FROM Location l WHERE l.compte.id = :compteId AND l.film.id = :filmId AND l.dateFin IS NULL")
    List<Location> findEnCoursByCompteIdAndFilmId(@Param("compteId") Long compteId, @Param("filmId") Long filmId);

}
