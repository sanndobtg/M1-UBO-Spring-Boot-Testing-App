package com.repositories;

import com.entities.Compte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {
    Optional<Compte> findByPseudoAndMotDePasse(String pseudo, String motDePasse);
    Optional<Compte> findByPseudo(String pseudo);
}