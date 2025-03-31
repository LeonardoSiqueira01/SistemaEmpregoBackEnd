package com.lgs.controller;

import java.util.List;
import java.util.stream.Collectors;

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
            @RequestParam(value = "specialties", required = false) String specialties,
            @RequestParam(value = "cidadeEstado", required = false) String cidadeEstado) {

        List<ProfessionalDTO> professionals = professionalService.getAllProfessionals();

        if (cidadeEstado != null && !cidadeEstado.isBlank()) {
            // Corrige a formatação da entrada
            String cidadeEstadoCorrigido = corrigirCidadeEstado(cidadeEstado);
            System.out.println("Filtrando por cidade e estado corrigido: " + cidadeEstadoCorrigido);

            // Criamos uma versão alternativa substituindo "-" por "/"
            String cidadeEstadoAlternativo = cidadeEstadoCorrigido.replace(" - ", " / ");

            // Aplica o filtro para cidade e estado considerando os dois formatos
            professionals = professionals.stream()
                    .filter(professional -> professional.getLocation() != null &&
                            (professional.getLocation().toLowerCase().contains(cidadeEstadoCorrigido.toLowerCase()) ||
                             professional.getLocation().toLowerCase().contains(cidadeEstadoAlternativo.toLowerCase())))
                    .collect(Collectors.toList());
        }


        // Filtra profissionais por especialidade, se fornecida
        if (specialties != null && !specialties.isEmpty()) {
            professionals = professionals.stream()
                    .filter(professional -> professional.getSpecialties() != null && professional.getSpecialties().contains(specialties))
                    .collect(Collectors.toList());
        }

        // Após aplicar os filtros, exiba a lista de profissionais filtrados
        professionals.forEach(professional -> {
            System.out.println("Profissional filtrado: " + professional.getName() +
                    " | Especialidades: " + professional.getSpecialties() +
                    " | Localização: " + professional.getLocation());
        });

        return ResponseEntity.ok(professionals);
    }

    // Método para corrigir a duplicação do estado na string de cidadeEstado
    private String corrigirCidadeEstado(String cidadeEstado) {
        // Verifica se a cidadeEstado contém o estado repetido e remove
        String[] partes = cidadeEstado.split("-");
        if (partes.length == 3 && partes[1].trim().equals(partes[2].trim())) {
            // Remove a parte repetida (o estado duplicado)
            return partes[0].trim() + " - " + partes[1].trim();
        }
        return cidadeEstado.trim();
    }

    // Ajustada para retornar apenas Cidade - Estado
    private String getCityStateFromAddress(String location) {
        if (location != null) {
            String[] parts = location.split(",");

            if (parts.length >= 2) {
                // Obtém a última e penúltima parte como Estado e Cidade
                String estado = parts[parts.length - 1].trim();
                String cidade = parts[parts.length - 2].trim();

                // Se cidade contém hífen, pode ser "Cidade - Estado", então separa
                if (cidade.contains("-")) {
                    String[] cidadeEstado = cidade.split("-");
                    cidade = cidadeEstado[0].trim();
                    estado = cidadeEstado[1].trim();
                }

                // Retorna apenas a cidade e o estado
                return cidade + " - " + estado;
            }
        }
        return "";
    }
}
