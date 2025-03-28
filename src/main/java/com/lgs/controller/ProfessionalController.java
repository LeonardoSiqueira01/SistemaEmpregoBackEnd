package com.lgs.controller;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lgs.dto.ProfessionalDtoPerfil;
import com.lgs.entities.Professional;
import com.lgs.entities.Service;
import com.lgs.entities.User;
import com.lgs.repositories.ProfessionalRepository;
import com.lgs.repositories.ServiceRepository;
import com.lgs.repositories.UserRepository;
import com.lgs.security.TokenService;
import com.lgs.services.ServiceService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/professionals")
public class ProfessionalController {

	@Autowired
	private ProfessionalRepository professionalRepository;
	
	@Autowired
	private UserRepository userRepository;
	
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
    
    @PreAuthorize("hasRole('PROFESSIONAL')")
    @PutMapping("/{email}/edit")
    public ResponseEntity<?> editarPerfil(@PathVariable("email") String email,
                                          @RequestBody ProfessionalDtoPerfil dadosAtualizados,
                                          @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Verificar se o token foi passado no header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou inválido.");
            }

            // Extrair o token
            String token = authorizationHeader.substring(7); // Remove o prefixo "Bearer "
            String emailToken = tokenService.extractEmail(token);

            if (emailToken == null || !emailToken.equals(email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou não corresponde ao e-mail.");
            }

            // Busca o profissional pelo e-mail
            Optional<Professional> professionalOptional = professionalRepository.findByEmail(email);
            if (professionalOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profissional não encontrado.");
            }
            // Verificar se o novo e-mail já está registrado
            if (dadosAtualizados.getEmail() != null && !dadosAtualizados.getEmail().equals(email)) {
                Optional<User> existingEmail = userRepository.findByEmail(dadosAtualizados.getEmail());
                if (existingEmail.isPresent()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este e-mail já está registrado em outra conta.");
                }
            }
            // Atualiza os dados do profissional
            Professional profissional = professionalOptional.get();

            // Atualizar nome, email e localização se não forem nulos
            if (dadosAtualizados.getName() != null) {
                profissional.setName(dadosAtualizados.getName());
            }
            
            if (dadosAtualizados.getName() != null && !dadosAtualizados.getName().equals(profissional.getName())) {
                profissional.setName(dadosAtualizados.getName());
            }

            if (dadosAtualizados.getEmail() != null) {
                profissional.setEmail(dadosAtualizados.getEmail());
            }
            if (dadosAtualizados.getLocation() != null) {
                profissional.setLocation(dadosAtualizados.getLocation());
            }

            // Processar especialidades a serem removidas e adicionadas, se não estiverem vazias
            boolean specialtiesProcessed = false;

            // Processa especialidades a serem removidas, se houver
            if (dadosAtualizados.getSpecialtiesToRemove() != null && !dadosAtualizados.getSpecialtiesToRemove().isEmpty()) {
                String[] specialtiesToRemove = dadosAtualizados.getSpecialtiesToRemove().split(";");
                for (String specialty : specialtiesToRemove) {
                    profissional.removeSpecialty(specialty.trim()); // Remove espaços extras
                }
                specialtiesProcessed = true; // Indicando que pelo menos uma especialidade foi processada
            }

            // Processa especialidades a serem adicionadas, se houver
            if (dadosAtualizados.getSpecialties() != null && !dadosAtualizados.getSpecialties().isEmpty()) {
                String[] specialties = dadosAtualizados.getSpecialties().split(";");
                for (String specialty : specialties) {
                    String trimmedSpecialty = specialty.trim();

                    // Verificar se a especialidade já existe
                    if (profissional.getSpecialties().contains(trimmedSpecialty)) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Você já adicionou a especialidade: " + trimmedSpecialty);
                    }

                    // Adiciona a especialidade se não existir
                    profissional.addSpecialty(trimmedSpecialty);
                }
                specialtiesProcessed = true; // Indicando que pelo menos uma especialidade foi processada
            }

            // Se ambos os campos estiverem vazios, nada é feito (não processa nada)
            if (!specialtiesProcessed) {
                // Ambos os campos (specialties e specialtiesToRemove) estão vazios, então nada será processado
                professionalRepository.save(profissional);
                return ResponseEntity.ok("Perfil atualizado com sucesso, sem alterações nas especialidades.");
            }

            // Salva as alterações no banco de dados
            professionalRepository.save(profissional);

            return ResponseEntity.ok("Perfil atualizado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o perfil: " + e.getMessage());
        }
    }

    
    
    
    @PreAuthorize("hasRole('PROFESSIONAL')")
    @GetMapping("/{email}/specialties")
    public ResponseEntity<?> listarEspecialidades(@PathVariable("email") String email, 
                                                 @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Verificar se o token foi passado no header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou inválido.");
            }

            // Extrair o token
            String token = authorizationHeader.substring(7); // Remove o prefixo "Bearer "
            
            // Validar o token e extrair o e-mail
            String emailToken = tokenService.extractEmail(token);
            if (emailToken == null || !emailToken.equals(email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou não corresponde ao e-mail.");
            }

            // Busca o profissional pelo e-mail
            Optional<Professional> professionalOptional = professionalRepository.findByEmail(email);
            if (professionalOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profissional não encontrado.");
            }

            // Retorna as especialidades do profissional
            Professional professional = professionalOptional.get();
            return ResponseEntity.ok(professional.getSpecialties());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar especialidades: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('PROFESSIONAL')")
    @GetMapping("/{email}/profile")
    public ResponseEntity<?> obterPerfil(@PathVariable("email") String email,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Verificar se o token foi passado no header
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não fornecido ou inválido.");
            }

            // Extrair o token
            String token = authorizationHeader.substring(7); // Remove o prefixo "Bearer "

            // Validar o token e extrair o e-mail
            String emailToken = tokenService.extractEmail(token);
            if (emailToken == null || !emailToken.equals(email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido ou não corresponde ao e-mail.");
            }

            // Busca o profissional pelo e-mail
            Optional<Professional> professionalOptional = professionalRepository.findByEmail(email);
            if (professionalOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profissional não encontrado.");
            }

            // Recupera os dados do profissional
            Professional professional = professionalOptional.get();

            // Cria um DTO para retornar apenas as informações necessárias
            ProfessionalDtoPerfil perfilDto = new ProfessionalDtoPerfil();
            perfilDto.setName(professional.getName());
            perfilDto.setEmail(professional.getEmail());
            perfilDto.setLocation(professional.getLocation());
            perfilDto.setSpecialties(professional.getSpecialties());

            // Retorna os dados do perfil
            return ResponseEntity.ok(perfilDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao obter perfil: " + e.getMessage());
        }
    }

}