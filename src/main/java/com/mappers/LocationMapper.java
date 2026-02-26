package com.mappers;

import com.dtos.LocationDto;
import com.entities.Location;
import org.springframework.stereotype.Component;

/**
 * Mapper responsable de la conversion entre les entités Location et les DTOs LocationDto.
 * Un mapper permet de séparer la couche de persistance de la couche de présentation.
 *
 * Points clés du pattern Mapper :
 * - Conversion unidirectionnelle (Entity → DTO uniquement, car une Location
 *   est créée directement dans le service, pas depuis un DTO)
 * - Gestion des null-safety
 * - Accès aux relations JPA (Compte et Film) pour enrichir le DTO
 * - Pas de logique métier, uniquement de la transformation
 */
@Component
public class LocationMapper {

    /**
     * Convertit une entité Location en DTO LocationDto.
     * Cette méthode est utilisée pour exposer les données aux clients de l'API.
     *
     * Le DTO est enrichi avec des informations provenant des relations :
     * - pseudo : récupéré depuis l'entité Compte liée
     * - filmId et titreFilm : récupérés depuis l'entité Film liée
     * - enCours : calculé à partir de dateFin (null = location en cours)
     *
     * @param location l'entité à convertir
     * @return le DTO correspondant ou null si l'entité est null
     */
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
