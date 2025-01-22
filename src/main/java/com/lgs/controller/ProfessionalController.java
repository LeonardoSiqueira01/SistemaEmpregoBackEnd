package com.lgs.controller;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lgs.entities.Professional;
import com.lgs.entities.Service;
import com.lgs.repositories.ProfessionalRepository;
import com.lgs.repositories.ServiceRepository;
import com.lgs.security.TokenService;
import com.lgs.services.ServiceService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {

	@Autowired
	private ProfessionalRepository professionalRepository;
	
	@Autowired
	private TokenService tokenService;
	
	  @Autowired
	    private ServiceRepository serviceRepository;

    @Autowired
    private ServiceService serviceService;
    @PostMapping("/{email}/services/{serviceId}/accept")
    public ResponseEntity<?> aceitarServico(@PathVariable("email") String email, @PathVariable("serviceId") Long serviceId) {
        try {
            // Chama o serviço para aceitar o serviço
            serviceService.aceitarOuRecusarServico(serviceId, email, true);
            return ResponseEntity.ok("Serviço aceito com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{email}/services/{serviceId}/reject")
    public ResponseEntity<?> recusarServico(@PathVariable("email") String email, @PathVariable("serviceId") Long serviceId) {
        try {
            // Chama o serviço para recusar o serviço
            serviceService.aceitarOuRecusarServico(serviceId, email, false);
            return ResponseEntity.ok("Serviço recusado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{professionalId}/services/{serviceId}/request")
    public ResponseEntity<?> solicitarVinculo(@PathVariable("professionalId") Long professionalId, @PathVariable("serviceId") Long serviceId) {
        try {
            serviceService.vincularProfissionalComAceiteProfissional(serviceId, professionalId);
            return ResponseEntity.ok("Solicitação de vínculo enviada com sucesso!");
        } catch (RuntimeException | MessagingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    
    @PreAuthorize("hasRole('PROFESSIONAL')")
    @GetMapping("/me")
    public ResponseEntity<?> listarServicosSolicitadosDoProfissional(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Verificar se o token foi passado no header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou inválido.");
            }

            // Extrair o token
            String token = authorizationHeader.substring(7); // Remove o prefixo "Bearer "
            
            // Validar o token e extrair o e-mail
            String email = tokenService.extractEmail(token);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido.");
            }

            // Busca o profissional pelo e-mail
            Optional<Professional> professionalOptional = professionalRepository.findByEmail(email);
            if (professionalOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profissional não encontrado.");
            }

            Professional professional = professionalOptional.get();

            // Verificar se a lista de solicitações é null e inicializar se necessário
            Set<Long> solicitacoesVinculacaoServico = professional.getSolicitacoesVinculacaoServico();
            if (solicitacoesVinculacaoServico == null) {
                solicitacoesVinculacaoServico = new HashSet<>();
            }

            // Buscar os serviços solicitados para vinculação
            Set<Service> servicosSolicitados = new HashSet<>();
            for (Long serviceId : solicitacoesVinculacaoServico) {
                Optional<Service> serviceOptional = serviceRepository.findById(serviceId);
                serviceOptional.ifPresent(servicosSolicitados::add);
            }

            // Retorna a lista de serviços solicitados
            return ResponseEntity.ok(servicosSolicitados);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar serviços solicitados: " + e.getMessage());
        }
    }


    
    public Optional<com.lgs.entities.Service> listarServicosPorId(Long serviceId) {
        return serviceRepository.findById(serviceId);
    }
    
}
