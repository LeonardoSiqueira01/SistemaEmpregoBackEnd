package com.lgs.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lgs.dto.ApiResponse;
import com.lgs.dto.ProfessionalDTO;
import com.lgs.dto.RatingDTO;
import com.lgs.dto.ServiceDTO;
import com.lgs.entities.Client;
import com.lgs.entities.Professional;
import com.lgs.entities.Rating;
import com.lgs.entities.Service;
import com.lgs.entities.Service.ServiceStatus;
import com.lgs.entities.User;
import com.lgs.repositories.ClientRepository;
import com.lgs.repositories.ProfessionalRepository;
import com.lgs.repositories.ServiceRepository;
import com.lgs.repositories.UserRepository;
import com.lgs.security.TokenService;
import com.lgs.services.ServiceService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
	

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private ServiceRepository ServiceRepository;
    @Autowired
    private UserRepository UserRepository;
    
    @Autowired
    private ClientRepository clientRepository;


    @Autowired
    private ProfessionalRepository professionalRepository;

    
    // Método para criar serviço, passando o e-mail para buscar o cliente pelo e-mail
    @PostMapping("/{email}")
    public ResponseEntity<Service> createService(@PathVariable("email") String email, @RequestBody Service service) {
        try {
            // Busca o cliente pelo e-mail
            Optional<Client> clientOptional = clientRepository.findByEmail(email);
            
            // Verifica se o cliente foi encontrado
            if (clientOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Cliente não encontrado
            }

            // Obtém o id do cliente
            Long clientId = clientOptional.get().getId();

            // Cria o serviço para o cliente usando o id
            service = serviceService.criarServico(service, clientId);

            return new ResponseEntity<>(service, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping("/{serviceId}")
    public ResponseEntity<?> deleteService(@PathVariable("serviceId") Long serviceId, 
 @AuthenticationPrincipal User user) {
        Optional<Service> serviceOptional = ServiceRepository.findById(serviceId);

        if (serviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Serviço não encontrado"));
        }

        Service service = serviceOptional.get();

        if (!service.getClient().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse(false, "Você não tem permissão para excluir este serviço"));
        }
        ServiceRepository.deleteById(serviceId);
        return ResponseEntity.ok(new ApiResponse(true, "Serviço excluído com sucesso"));
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/{serviceId}/vincularProfissional/{professionalId}")
    public ResponseEntity<String> vincularProfissionalComAceite(
            @PathVariable("serviceId") Long serviceId,
            @PathVariable("professionalId") Long professionalId) {
        try {
            // Chama o método do serviço para vincular o profissional
            serviceService.vincularProfissionalComAceite(serviceId, professionalId);

            // Retorna uma resposta de sucesso
            return ResponseEntity.ok("O profissional foi notificado para aceitar ou recusar o serviço.");
        } catch (RuntimeException e) {
            // Retorna erro de validação de negócio
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Retorna erro genérico
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a solicitação.");
        }
    }


    
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceDTO> getServiceById(@PathVariable("id") Long id) {
        ServiceDTO service = serviceService.findById(id);
        if (service == null) {
            return ResponseEntity.notFound().build(); // Retorna 404 se o serviço não for encontrado
        }
        return ResponseEntity.ok(service); // Retorna 200 com os detalhes do serviço
    }
    
    @GetMapping("/{serviceId}/status")
    public ResponseEntity<?> getServiceStatus(@PathVariable Long serviceId) {
        try {
            com.lgs.entities.Service service = (Service) serviceService.getServiceById(serviceId);
            return ResponseEntity.ok(service);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/{serviceId}/finalizar")
    public ResponseEntity<Service> finalizarServico(@PathVariable("serviceId") Long serviceId, @RequestBody Rating clienteRating) {
        try {
            Service service = serviceService.finalizarServico(serviceId, clienteRating);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
           System.out.println( e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    
    
    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/{serviceId}")
    public ResponseEntity<Service> atualizarServico(
        @PathVariable("serviceId") Long serviceId, // Anotação explícita
        @RequestBody Service serviceAtualizado,
        @RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Verificar se o token foi fornecido
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Token não fornecido ou inválido
            }

            String token = authorizationHeader.substring(7); // Remove "Bearer "
            String email = tokenService.extractEmail(token);

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // Token inválido
            }

            Optional<Client> clientOptional = clientRepository.findByEmail(email);
            if (clientOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Cliente não encontrado
            }

            Client client = clientOptional.get();

            // Verificar se o serviço pertence ao cliente
            Optional<Service> serviceOptional = ServiceRepository.findById(serviceId);
            if (serviceOptional.isEmpty() || !serviceOptional.get().getClient().equals(client)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null); // Serviço não encontrado ou não pertence ao cliente
            }

            // Atualiza as informações do serviço
            Service serviceExistente = serviceOptional.get();
            serviceExistente.setName(serviceAtualizado.getName());
            serviceExistente.setDescription(serviceAtualizado.getDescription());
            serviceExistente.setLocation(serviceAtualizado.getLocation());
            serviceExistente.setSpecialty(serviceAtualizado.getSpecialty());
            	
            // Salva o serviço atualizado
            ServiceRepository.save(serviceExistente);
            return ResponseEntity.ok(serviceExistente); // Retorna o serviço atualizado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Erro interno do servidor
        }
    }


    // Aceitar Serviço - Somente PROFESSIONAL pode aceitar um serviço
    @PreAuthorize("hasRole('PROFESSIONAL')")
    @PutMapping("/{serviceId}/aceitar")
    public ResponseEntity<Service> aceitarServico(@PathVariable Long serviceId) {
        try {
            Service service = serviceService.aceitarServico(serviceId);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    


    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/me")
    public ResponseEntity<?> listarServicosDoCliente(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestParam(value = "cidadeEstado", required = false) String cidadeEstado,
        @RequestParam(value = "specialty", required = false) String specialty,
        @RequestParam(value = "status", required = false) String status) {
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

            // Busca o cliente pelo e-mail
            Optional<Client> clientOptional = clientRepository.findByEmail(email);
            if (clientOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente não encontrado.");
            }

            // Buscar serviços do cliente
            Long clientId = clientOptional.get().getId();
            List<Service> services;

            // Se a especialidade for fornecida, filtra os serviços por especialidade
            if (specialty != null && !specialty.isEmpty()) {
                services = serviceService.listarServicosPorEspecialidade(clientId, specialty);
            } else {
                services = serviceService.listarServicosPorCliente(clientId);
            }

            // Ordena os serviços com base no status (ABERTO -> INICIADO -> FINALIZADO)
            services.sort((s1, s2) -> {
                int statusPriority1 = getStatusPriority(s1.getStatus());
                int statusPriority2 = getStatusPriority(s2.getStatus());
                return Integer.compare(statusPriority1, statusPriority2);
            });
            if (status != null && !status.isBlank()) {
            	services = services.stream()
                        .filter(service -> service.getStatus() != null && status.equalsIgnoreCase(service.getStatus().name()))
                        .collect(Collectors.toList());
            }
            System.out.println("cidade antes de corrigir: "+cidadeEstado);
            
            if (cidadeEstado != null && !cidadeEstado.isBlank()) {
                String cidadeEstadoCorrigido = corrigirCidadeEstado(cidadeEstado);
                System.out.println("cidadeEstadoCorrigido: "+cidadeEstadoCorrigido);
                System.out.println("cidade antes de corrigir: "+cidadeEstado);
                services = services.stream()
                        .filter(service -> cidadeEstadoCorrigido.equalsIgnoreCase(getCityStateFromAddress(service.getLocation())))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(services);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar serviços: " + e.getMessage());
        }
    }

    // Método auxiliar para obter a prioridade do status
    private int getStatusPriority(ServiceStatus serviceStatus) {
        switch (serviceStatus) {
            case ABERTO:
                return 1;
            case INICIADO:
                return 2;
            case FINALIZADO:
                return 3;
            default:
                return Integer.MAX_VALUE; // Se houver outro status desconhecido
        }
    }
    @GetMapping
    public ResponseEntity<?> listarTodosServicos(
            @RequestParam(value = "cidadeEstado", required = false) String cidadeEstado,
            @RequestParam(value = "especialidade", required = false) String especialidade) {
        try {
            // Lista todos os serviços inicialmente
            List<Service> servicos = serviceService.listarTodosServicos();

            // Filtrar apenas serviços com status ABERTO
            servicos = servicos.stream()
                    .filter(service -> service.getStatus() == Service.ServiceStatus.ABERTO)
                    .collect(Collectors.toList());

            // Filtrar por Cidade - Estado
            if (cidadeEstado != null && !cidadeEstado.isBlank()) {
                System.out.println("Filtrando por cidade e estado: " + cidadeEstado);

                // Remover o estado duplicado se presente
                String cidadeEstadoCorrigido = corrigirCidadeEstado(cidadeEstado);
          

                servicos = servicos.stream()
                        .filter(service -> cidadeEstadoCorrigido.equalsIgnoreCase(getCityStateFromAddress(service.getLocation())))
                        .collect(Collectors.toList());
            }

            System.out.println("Valor recebido em cidadeEstado: " + cidadeEstado);

            // Filtrar por especialidade
            if (especialidade != null && !especialidade.isBlank()) {
                servicos = servicos.stream()
                        .filter(service -> especialidade.equalsIgnoreCase(service.getSpecialty()))
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(servicos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao listar serviços: " + e.getMessage());
        }
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

                return cidade + " - " + estado;
            }
        }
        return "";
    }




    
    @PostMapping("/{serviceId}/professional/{professionalEmail}/rate-client")
    public ResponseEntity<String> avaliarCliente(
            @PathVariable("serviceId") Long serviceId,
            @PathVariable("professionalEmail") String professionalEmail,
            @RequestBody RatingDTO	 ratingDTO
    ) {
        try {
            // Chama o método do serviço para avaliar o cliente
            serviceService.avaliarCliente(serviceId, professionalEmail, ratingDTO.getRating(), ratingDTO.getComment());

            // Retorna uma resposta de sucesso
            return ResponseEntity.ok("Avaliação registrada com sucesso!");
        } catch (Exception e) {
            // Retorna um erro se algo falhar
            return ResponseEntity.status(400).body("Erro ao registrar a avaliação: " + e.getMessage());
        }
    }
    
    
    @PreAuthorize("hasRole('PROFESSIONAL')")
    @PostMapping("/{professionalEmail}/solicitar/{serviceId}")
    public ResponseEntity<?> solicitarServicoAoCliente(
            @PathVariable("professionalEmail") String professionalEmail, // Email do profissional
            @PathVariable("serviceId") Long serviceId) {         // ID do cliente ao qual o profissional quer fazer a solicitação

        try {
            // Verificar se o profissional existe pelo email
            Professional professional = professionalRepository.findByEmail(professionalEmail)
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

            // Chama o serviço para o profissional solicitar o serviço ao cliente
            com.lgs.entities.Service service = serviceService.solicitarServicoAoCliente(professional.getId(), serviceId);

            // Retorna resposta com status 200 OK e o serviço solicitado
            return ResponseEntity.ok(service);

        }catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("mensagem", "Erro ao solicitar serviço: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (MessagingException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("mensagem", "Erro ao enviar e-mail: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }
    
    
    @PostMapping("/{ProfessionalEmail}/services/{serviceId}/accept")
    public ResponseEntity<?> aceitarServico(@PathVariable("ProfessionalEmail") String ProfessionalEmail, @PathVariable("serviceId") Long serviceId) throws MessagingException {
        try {
            // Chama o serviço para aceitar o serviço
            serviceService.aceitarOuRecusarSolicitacao(serviceId, ProfessionalEmail, true); // Passa 'true' para aceitar
            return ResponseEntity.ok("Serviço aceito com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{ProfessionalEmail}/services/{serviceId}/reject")
    public ResponseEntity<?> recusarServico(@PathVariable("ProfessionalEmail") String ProfessionalEmail, @PathVariable("serviceId") Long serviceId) throws MessagingException {
        try {
            // Chama o serviço para recusar o serviço
            serviceService.aceitarOuRecusarSolicitacao(serviceId, ProfessionalEmail, false); // Passa 'false' para recusar
            return ResponseEntity.ok("Serviço recusado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{serviceId}/profissionais")
    public List<ProfessionalDTO> listarProfissionaisVinculados(@PathVariable("serviceId") Long serviceId) {
        // Buscar o serviço pelo ID
        Service service = ServiceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // Chamar o método na classe Service para listar os IDs dos profissionais vinculados
        List<Professional> profissionais = service.listarProfissionaisVinculados(professionalRepository);

        // Mapear a lista de profissionais para ProfessionalDTO
        List<ProfessionalDTO> professionalDTOs = profissionais.stream().map(professional -> {
            return new ProfessionalDTO(
                    serviceId, professional.getName(),
                    professional.getEmail(),
                    professional.getTotalServicesCompleted(),
                    professional.getAverageRating(),
                    professional.isAvailable(),
                    professional.getSpecialties(),
                    professional.getLocation()
            );
        }).collect(Collectors.toList());

        // Retorna a lista de ProfessionalDTO
        return professionalDTOs;
    }

}