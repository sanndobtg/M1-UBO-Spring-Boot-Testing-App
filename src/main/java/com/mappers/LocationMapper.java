package com.mappers;

import com.dtos.LocationDto;
import com.entities.Location;
import org.springframework.stereotype.Component;

//TODO : documentation
@Component
public class LocationMapper {
    public LocationDto toDto(Location location){
        if (location == null){
            return null;
        }

        LocationDto locationDto = new LocationDto();
        locationDto.setId(location.getId());
        locationDto.setPseudo(location.getCompte().getPseudo());
        locationDto.setFilmId(location.getFilm().getId());
        locationDto.setTitreFilm(location.getFilm().getTitre());
        locationDto.setDateDebut(location.getDateDebut());
        locationDto.setDateFin(location.getDateFin());
        locationDto.setEnCours(location.getDateFin() == null);

        return locationDto;
    }
}
