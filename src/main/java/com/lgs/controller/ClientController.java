package com.lgs.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lgs.dto.ProfessionalDTO;
import com.lgs.services.ProfessionalService;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    private final ProfessionalService professionalService;

    public ClientController(ProfessionalService professionalService) {
        this.professionalService = professionalService;
    }
    @GetMapping
    public ResponseEntity<List<ProfessionalDTO>> getAllProfessionals(
            @RequestParam(value = "specialties", required = false) String specialties) {
        List<ProfessionalDTO> professionals;

        if (specialties != null && !specialties.isEmpty()) {
            professionals = professionalService.getProfessionalsBySpecialty(specialties);
        } else {
            professionals = professionalService.getAllProfessionals();
        }

        return ResponseEntity.ok(professionals);
    }


}
