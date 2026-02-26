package com.controllers;

import com.dtos.LocationDto;
import com.services.impl.LocationServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final LocationServiceImpl locationService;

    public ReservationController(LocationServiceImpl locationService) {
        this.locationService = locationService;
    }

    /**
     * GET /reservations/me
     * Récupère la liste des films loués par l'utilisateur connecté.
     */
    @GetMapping("/me")
    public List<LocationDto> getFilmsEnLocation(@RequestHeader("X-User-Pseudo") String pseudo) {
        return locationService.getLocationsEnCours(pseudo);
    }

    /**
     * POST /reservations/{titreFilm}
     * Réserver un film.
     */
    @PostMapping("/{titreFilm}")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto reserverFilm(@PathVariable String titreFilm,
                                    @RequestHeader("X-User-Pseudo") String pseudo) {
        return locationService.reserverFilm(pseudo, titreFilm);
    }

    /**
     * DELETE /reservations/{titreFilm}
     * Terminer la location d'un film.
     */
    @DeleteMapping("/{titreFilm}")
    public LocationDto terminerLocation(@PathVariable String titreFilm,
                                        @RequestHeader("X-User-Pseudo") String pseudo) {
        return locationService.terminerLocation(pseudo, titreFilm);
    }
}