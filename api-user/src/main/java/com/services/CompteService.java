package com.services;

import com.dtos.CompteDto;

import java.util.List;

public interface CompteService {

    CompteDto saveCompte(CompteDto compteDto);

    CompteDto getCompteById(Long compteId);

    boolean deleteCompte(Long compteId);

    List<CompteDto> getAllComptes();

    CompteDto login(String pseudo, String motDePasse);

    CompteDto updateCompte(Long id, CompteDto compteDto);
}