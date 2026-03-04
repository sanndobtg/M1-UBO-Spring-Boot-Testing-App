package com.services.impl;


import com.dtos.CompteDto;
import com.entities.Compte;
import com.mappers.CompteMapper;
import com.repositories.CompteRepository;
import com.services.CompteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service("compteService")
@Transactional
public class CompteServiceImpl implements CompteService {

    @Autowired
    private CompteRepository compteRepository;

    @Autowired
    private CompteMapper compteMapper;


    @Override
    public CompteDto saveCompte(CompteDto compteDto) {
        Compte monCompte = new Compte();
        Compte compteSauvegarde = new Compte();

        monCompte = compteMapper.toEntity(compteDto);
        compteSauvegarde =compteRepository.save(monCompte);

        return compteMapper.toDto(compteSauvegarde);
    }

    @Override
    @Transactional(readOnly = true)
    public CompteDto getCompteById(Long compteId) {
        var compte = compteRepository.findById(compteId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le compte avec l'ID %d n'existe pas", compteId)));

        return compteMapper.toDto(compte);
    }

    @Override
    public boolean deleteCompte(Long compteId) {
        compteRepository.deleteById(compteId);

        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompteDto> getAllComptes() {
        return compteRepository.findAll().stream()
                .map(compteMapper::toDto)
                .toList();
    }

    public CompteDto updateCompte(Long id, CompteDto compteDto) {
        Compte compte = compteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Le compte avec l'ID %d n'existe pas", id)));

        // Met à jour uniquement les champs non null
        if (compteDto.getPseudo() != null) compte.setPseudo(compteDto.getPseudo());
        if (compteDto.getNom() != null) compte.setNom(compteDto.getNom());
        if (compteDto.getPrenom() != null) compte.setPrenom(compteDto.getPrenom());
        if (compteDto.getDateDeNaissance() != null) compte.setDateDeNaissance(compteDto.getDateDeNaissance());
        if (compteDto.getAdresse() != null) compte.setAdresse(compteDto.getAdresse());
        if (compteDto.getRole() != null) compte.setRole(compteDto.getRole());
        if (compteDto.getMdp() != null) compte.setMotDePasse(compteDto.getMdp());

        return compteMapper.toDto(compteRepository.save(compte));
    }

}