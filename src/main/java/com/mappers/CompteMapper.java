package com.mappers;

import com.dtos.CompteDto;
import com.entities.Compte;
import org.springframework.stereotype.Component;

@Component
public class CompteMapper {


    public CompteDto toDto(Compte compte) {
        if (compte == null) {
            return null;
        }

        CompteDto compteDto = new CompteDto();
        compteDto.setId(compte.getId());
        compteDto.setAdresse(compte.getAdresse());
        compteDto.setMdp(compte.getMdp());
        compteDto.setNom(compte.getNom());
        compteDto.setPrenom(compte.getPrenom());
        compteDto.setPseudo(compte.getPseudo());
        compteDto.setDateDeNaissance(compte.getDateDeNaissance());
        compteDto.setRole(compte.getRole());

        return compteDto;
    }

    public Compte toEntity(CompteDto compteDto) {
        if (compteDto == null) {
            return null;
        }

        Compte compte = new Compte();
        if (compteDto.getId() != null) {
            compte.setId(compteDto.getId());
        }
        compte.setAdresse(compteDto.getAdresse());
        compte.setMdp(compteDto.getMdp());
        compte.setNom(compteDto.getNom());
        compte.setPrenom(compteDto.getPrenom());
        compte.setPseudo(compteDto.getPseudo());
        compte.setDateDeNaissance(compteDto.getDateDeNaissance());
        compte.setRole(compteDto.getRole());
        return compte;
    }

}