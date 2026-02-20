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


    @GetMapping("/me")
    public List<LocationDto> getFilmsEnLocation(@RequestHeader("X-User-Pseudo") String pseudo) {
        return locationService.getLocationsEnCours(pseudo);
    }


    @PostMapping("/{titreFilm}")
    @ResponseStatus(HttpStatus.CREATED)
    public LocationDto reserverFilm(@PathVariable String titreFilm,
                                    @RequestHeader("X-User-Pseudo") String pseudo) {
        return locationService.reserverFilm(pseudo, titreFilm);
    }


    @DeleteMapping("/{titreFilm}")
    public LocationDto terminerLocation(@PathVariable String titreFilm,
                                        @RequestHeader("X-User-Pseudo") String pseudo) {
        return locationService.terminerLocation(pseudo, titreFilm);
    }
}