package com.lgs.services;

import com.lgs.dto.ProfessionalDTO;
import com.lgs.entities.Professional;
import com.lgs.repositories.ProfessionalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfessionalService {

    private final ProfessionalRepository professionalRepository;

    public ProfessionalService(ProfessionalRepository professionalRepository) {
        this.professionalRepository = professionalRepository;
    }

    public List<ProfessionalDTO> getAllProfessionals() {
        // Converte a lista de Professional para ProfessionalDTO
        return professionalRepository.findAll().stream()
                .map(professional -> new ProfessionalDTO(
                        professional.getId(),
                        professional.getName(),
                        professional.getEmail(),
                        professional.getTotalServicesCompleted(),
                        professional.getAverageRating(),
                        professional.isAvailable(),
                        professional.getSpecialties(),
                        professional.getLocation()
                ))
                .collect(Collectors.toList());
    }
}
