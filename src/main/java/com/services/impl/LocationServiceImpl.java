package com.services.impl;

import com.dtos.LocationDto;
import com.entities.Location;
import com.mappers.LocationMapper;
import com.repositories.CompteRepository;
import com.repositories.FilmRepository;
import com.repositories.LocationRepository;
import com.services.LocationService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implémentation des opérations métier pour la gestion des locations de films.
 * Cette classe suit le principe de Single Responsibility (SOLID).
 *
 * Contraintes UML respectées :
 * - Un compte ne peut pas avoir plus de 3 locations avec dateFin null
 * - age user >= age film
 * - Compte loue si film louable
 */
@Service("locationService")
@Transactional
public class LocationServiceImpl implements LocationService {

    private static final int MAX_LOCATIONS_EN_COURS = 3;

    private final LocationRepository locationRepository;
    private final CompteRepository compteRepository;
    private final FilmRepository filmRepository;
    private final LocationMapper locationMapper;

    /**
     * Constructeur avec injection des dépendances.
     * L'injection par constructeur est préférée à @Autowired car :
     * - Elle rend les dépendances obligatoires
     * - Elle facilite les tests unitaires
     * - Elle permet l'immutabilité
     */
    public LocationServiceImpl(LocationRepository locationRepository,
                               CompteRepository compteRepository,
                               FilmRepository filmRepository,
                               LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.compteRepository = compteRepository;
        this.filmRepository = filmRepository;
        this.locationMapper = locationMapper;
    }

    /**
     * {@inheritDoc}
     * Vérifie toutes les contraintes métier avant de créer la location :
     * 1. Le film doit être louable
     * 2. Le compte ne doit pas dépasser 3 locations en cours
     * 3. L'âge de l'utilisateur doit être >= à l'âge minimum du film
     */
    @Override
    public LocationDto reserverFilm(String pseudo, String titreFilm) {

        // Récupérer le compte
        var compte = compteRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le compte avec le pseudo '%s' n'existe pas", pseudo)));

        // Récupérer le film par titre
        var film = filmRepository.findByTitre(titreFilm)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le film '%s' n'existe pas", titreFilm)));

        // Vérifier que le film est louable
        if (!film.isLouable()) {
            throw new IllegalStateException(
                    String.format("Le film '%s' n'est pas ouvert à la location", titreFilm));
        }

        // Vérifier le quota de 3 locations en cours max
        var nbEnCours = locationRepository.countByCompteIdAndDateFinIsNull(compte.getId());
        if (nbEnCours >= MAX_LOCATIONS_EN_COURS) {
            throw new IllegalStateException(
                    String.format("Quota de %d films en location atteint", MAX_LOCATIONS_EN_COURS));
        }

        // Vérifier l'âge minimum
        if (compte.getAge() < film.getAgeMinimum()) {
            throw new IllegalStateException(
                    String.format("Âge insuffisant (vous avez %d ans, minimum requis : %d ans)",
                            compte.getAge(), film.getAgeMinimum()));
        }

        // Créer la location (dateFin null = en cours)
        var location = new Location();
        location.setCompte(compte);
        location.setFilm(film);
        location.setDateDebut(LocalDate.now());
        location.setDateFin(null);

        var savedLocation = locationRepository.save(location);

        return locationMapper.toDto(savedLocation);

    }

    /**
     * {@inheritDoc}
     * Utilisation de l'API Stream pour trouver la location en cours du film concerné
     */
    @Override
    public LocationDto terminerLocation(String pseudo, String titreFilm) {

        // Récupérer le compte
        var compte = compteRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le compte avec le pseudo '%s' n'existe pas", pseudo)));

        // Récupérer le film
        var film = filmRepository.findByTitre(titreFilm)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le film '%s' n'existe pas", titreFilm)));

        // Trouver la location en cours pour ce compte et ce film
        var location = locationRepository
                .findEnCoursByCompteIdAndFilmId(compte.getId(), film.getId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Aucune location en cours pour le film '%s'", titreFilm)));

        // Terminer la location (mettre dateFin à aujourd'hui)
        location.setDateFin(LocalDate.now());
        var savedLocation = locationRepository.save(location);

        return locationMapper.toDto(savedLocation);

    }

    /**
     * {@inheritDoc}
     * Utilisation de l'API Stream pour une transformation fonctionnelle des données
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocationDto> getLocationsEnCours(String pseudo) {

        var compte = compteRepository.findByPseudo(pseudo)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le compte avec le pseudo '%s' n'existe pas", pseudo)));

        return locationRepository.findByCompteIdAndDateFinIsNull(compte.getId())
                .stream()
                .map(locationMapper::toDto)
                .toList();
    }

}