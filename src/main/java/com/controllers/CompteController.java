package com.controllers;

import com.dtos.CompteDto;
import com.services.impl.CompteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comptes")
public class CompteController {

    @Autowired
    private CompteServiceImpl compteService;

//    public CompteController(CompteServiceImpl compteService) {
//        this.compteService = compteService;
//    }


    @GetMapping
    public List<CompteDto> getComptes() {
        return compteService.getAllComptes();
    }

    @GetMapping("/{id}")
    public CompteDto getCompte(@PathVariable Long id){
        return compteService.getCompteById(id);
    }


    @PostMapping
    public CompteDto saveCompte(final @RequestBody CompteDto compteDto){
        return compteService.saveCompte(compteDto);
    }


    @DeleteMapping("/{id}")
    public Boolean deleteDog(@PathVariable Long id){
        return compteService.deleteCompte(id);
    }

}
