package com.lgs.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lgs.dto.ProfessionalServiceStatus;
import com.lgs.dto.ServiceDTO;
import com.lgs.entities.Client;
import com.lgs.entities.Professional;
import com.lgs.entities.Rating;
import com.lgs.entities.Service.ServiceStatus;
import com.lgs.entities.User;
import com.lgs.repositories.ClientRepository;
import com.lgs.repositories.ProfessionalRepository;
import com.lgs.repositories.RatingRepository;
import com.lgs.repositories.ServiceRepository;
import com.lgs.repositories.UserRepository;
import com.lgs.security.TokenService;

import jakarta.mail.MessagingException;

@Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired 
    private ClientRepository clientRepository;
    
    @Autowired
    private TokenService service;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RatingRepository ratingRepository;

    // Listar serviços de um cliente pelo e-mail
    public List<com.lgs.entities.Service> listarServicosPorEmailCliente(String email) {
        return serviceRepository.findByClientEmail(email);
    }
 // Aceitar serviço por parte do profissional
    public com.lgs.entities.Service aceitarServico(Long serviceId) {
        com.lgs.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        if (service.getStatus() != ServiceStatus.ABERTO) {
            throw new RuntimeException("Somente serviços abertos podem ser aceitos.");
        }

        // O profissional que vai aceitar o serviço deve ser o que está vinculado ao serviço
        Professional profissional = service.getProfessional();
        if (profissional == null || !profissional.isAvailable()) {
            throw new RuntimeException("O profissional não está disponível para aceitar este serviço.");
        }

        service.setStatus(ServiceStatus.INICIADO);  // Alterar status do serviço para INICIADO
        serviceRepository.save(service);

        // Aqui você pode adicionar alguma lógica de notificação, por exemplo, para o cliente
        try {
            emailService.enviarNotificacaoParaCliente(service.getClient(), service.getName(), null, true);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail para o cliente", e);
        }

        return service;
    }
    
    public com.lgs.entities.Service getServiceById(Long id) {
        Optional<com.lgs.entities.Service> service = serviceRepository.findById(id);
        if (service.isPresent()) {
            return  service.get();
        } else {
            throw new RuntimeException("Serviço com ID " + id + " não encontrado.");
        }
    }

	   public ServiceDTO findById(Long id) {
	        return serviceRepository.findById(id)
	                .map(service -> new ServiceDTO(
	                        service.getId(),
	                        service.getName(),
	                        service.getDescription(),
	                        service.getLocation(),
	                        service.getSpecialty()
	                ))
	                .orElse(null); // Retorna null se o ID não for encontrado
	    }
	
    

    public com.lgs.entities.Service criarServico(com.lgs.entities.Service service, Long id) {

        // Busca o cliente no banco de dados usando o email
        User client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        
        // Verifique se o usuário é um cliente
        if (!(client instanceof Client)) {
            throw new RuntimeException("Usuário não é um cliente");
        }

        Client cliente = (Client) client;

        // Verifique se o cliente está verificado
        if (!cliente.isVerified()) {
            throw new RuntimeException("Somente clientes com conta verificada podem cadastrar serviços.");
        }

        
        // Associe o cliente ao serviço
        service.setClient(cliente);
        service.setClientEmail(cliente.getEmail());
        service.setClientName(cliente.getName());
        service.setProfessional(null); // Profissional ainda não vinculado
        service.setStatus(ServiceStatus.ABERTO); // Status inicial é ABERTO
        service.setRatings(null); // Definir ratings como null, se necessário
        serviceRepository.save(service);

        // Atualize o total de serviços do cliente
        cliente.incrementTotalServicesRequested(); // Método que deve incrementar o total de serviços
        userRepository.save(cliente);

        return service;
    }
	  
 
    // Finalizar serviço
    public com.lgs.entities.Service finalizarServico(Long serviceId, Rating clienteRating) {
        com.lgs.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        if (service.getStatus() != ServiceStatus.INICIADO) {
            throw new RuntimeException("Somente serviços em andamento podem ser finalizados.");
        }

  

        if (clienteRating == null || clienteRating.getRating() == null) {
            throw new RuntimeException("A avaliação do profissional é obrigatória para finalizar o serviço.");
        }

        // Verifica se a nota está no intervalo permitido, por exemplo, de 1 a 5
        if (clienteRating.getRating() < 1 || clienteRating.getRating() > 5) {
            throw new RuntimeException("A nota deve estar entre 1 e 5.");
        }

        Client client = clientRepository.findById(service.getClient().getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Professional prof = professionalRepository.findById(service.getProfessional().getId())
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        // Preencher corretamente o Rating
        clienteRating.setService(service);
        clienteRating.setClient(client);
        clienteRating.setProfessional(prof);
        clienteRating.setRatedClient(null);  
        clienteRating.setRatedProfessional(clienteRating.getRating());
        client.incrementTotalServicesCompleted();
        ratingRepository.save(clienteRating);

        service.getRatings().add(clienteRating);
        service.setConclusaoServico(LocalDate.now()); 
        service.setStatus(ServiceStatus.FINALIZADO);
        serviceRepository.save(service);
        clientRepository.save(client);
        
        Professional profissional = service.getProfessional();
        if (profissional != null) {
            profissional.incrementTotalServicesCompleted();
            atualizarMediaAvaliacaoProfissional(profissional);
            profissional.setAvailable(true);
            userRepository.save(profissional);
        }

        return service;
    }


    
    // Listar serviços por status
    public List<com.lgs.entities.Service> listarServicosPorStatus(String status) {
        if (status != null) {
            try {
                ServiceStatus serviceStatus = ServiceStatus.valueOf(status.toUpperCase());
                return serviceRepository.findByStatus(serviceStatus);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Status inválido.");
            }
        }
        return serviceRepository.findAll();
    }

    // Iniciar serviço
    public com.lgs.entities.Service iniciarServico(Long servicoId) {
        com.lgs.entities.Service service = serviceRepository.findById(servicoId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        if (service.getStatus() != ServiceStatus.ABERTO) {
            throw new RuntimeException("Somente serviços aceitos podem ser iniciados.");
        }

        service.setStatus(ServiceStatus.INICIADO);
        return serviceRepository.save(service);
    }

    
    public void vincularProfissionalComAceite(Long serviceId, Long professionalId) throws MessagingException {
        // Obtém o serviço do repositório
        com.lgs.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // Verifica se o serviço já possui um profissional vinculado
        if (service.getProfessional() != null) {
            throw new RuntimeException("Este serviço já possui um profissional vinculado.");
        }

        // Obtém o profissional do repositório
        Professional professional = (Professional) userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        // Verifica se o profissional está disponível
        if (!professional.isAvailable()) {
            throw new RuntimeException("Este profissional não está disponível.");
        }
        	professional.addIdsSolicitacoesDeVinculacao(serviceId);
        	professional.addTotalServicesRequested();
        // Vincula o profissional ao serviço e atualiza o status
        service.setProfessional(null);
        if(professional.getServiceStatus() == ProfessionalServiceStatus.RECUSADO || professional.getServiceStatus() == ProfessionalServiceStatus.ABERTO) {
        professional.setServiceStatus(ProfessionalServiceStatus.AGUARDANDO); // Define status como AGUARDANDO 
        }
        serviceRepository.save(service);

        // Envia notificação ao profissional
        emailService.enviarNotificacaoParaProfissional(professional, service);
    }
    public void vincularProfissionalComAceiteProfissional(Long serviceId, Long professionalId) throws MessagingException {
        // Obtém o serviço do repositório
        com.lgs.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        // Verifica se o serviço já possui um profissional vinculado
        if (service.getProfessional() != null) {
            throw new RuntimeException("Este serviço já possui um profissional vinculado.");
        }

        // Obtém o profissional do repositório
        Professional professional = (Professional) userRepository.findById(professionalId)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

        // Verifica se o profissional está disponível
        if (!professional.isAvailable()) {
            throw new RuntimeException("Este profissional não está disponível.");
        }
        	professional.addIdsSolicitacoesDeVinculacao(serviceId);
        // Vincula o profissional ao serviço e atualiza o status
        service.setProfessional(null);
        if(professional.getServiceStatus() == ProfessionalServiceStatus.RECUSADO || professional.getServiceStatus() == ProfessionalServiceStatus.ABERTO) {
        professional.setServiceStatus(ProfessionalServiceStatus.AGUARDANDO); // Define status como AGUARDANDO 
        }
        serviceRepository.save(service);

        // Envia notificação ao profissional
        emailService.enviarNotificacaoParaProfissional(professional, service);
    }
    
    public void aceitarOuRecusarServico(Long serviceId, String email, boolean aceitar) {
        // Obtém o serviço do repositório
        com.lgs.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));
        
        // Verifica se o serviço já tem um profissional vinculado
        if (service.getProfessional() != null) {
            throw new RuntimeException("Este serviço já foi atribuído a um profissional.");
        }
        
        // Obtém o profissional do repositório
        Professional professional = (Professional) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));
        
        // Verifica se o profissional está disponível para aceitar o serviço
        if (!professional.isAvailable()) {
            professional.removeIdSolicitacaoDeVinculacao(serviceId);
            professionalRepository.save(professional);
            throw new RuntimeException("Você não está disponível para aceitar o serviço, pois já está vinculado a outro serviço.");
        }

        // Verifica se o profissional já possui um serviço em andamento (com status INICIADO)
        boolean temServicoEmAndamento = professional.getServices().stream()
                .anyMatch(s -> s.getStatus() == ServiceStatus.INICIADO);

        if (temServicoEmAndamento) {
            throw new RuntimeException("Este profissional já iniciou outro serviço. Não é possível aceitar um novo serviço.");
        }

        // Lógica de aceitação ou recusa
        if (aceitar) {
            // Profissional aceita o serviço
            service.setInicioServico(LocalDate.now()); // Define a data de início do serviço
            service.setProfessional(professional); // Associa o profissional ao serviço
            service.setStatus(ServiceStatus.INICIADO); // Altera o status do serviço
            service.setServiceStatus(ProfessionalServiceStatus.ACEITO); // Altera o status do serviço para ACEITO
            service.setProfessionalName(professional.getName());
            service.setProfessionalEmail(professional.getEmail());

            professional.setServiceStatus(ProfessionalServiceStatus.ACEITO); // Define status do profissional
            professional.setAvailable(false); // Marca o profissional como não disponível
            serviceRepository.save(service);
            professionalRepository.save(professional);

            try {
                emailService.enviarNotificacaoParaCliente(service.getClient(), service.getName(), professional.getName(), true);
            } catch (MessagingException e) {
                throw new RuntimeException("Erro ao enviar e-mail para o cliente", e);
            }
        } else {
            // Profissional recusa o serviço
            service.setStatus(ServiceStatus.ABERTO); // Reabre o serviço
            professional.setServiceStatus(ProfessionalServiceStatus.RECUSADO); // Define o status como RECUSADO
            serviceRepository.save(service);
            professional.removeIdSolicitacaoDeVinculacao(serviceId);
            professionalRepository.save(professional);

            try {
                emailService.enviarNotificacaoParaCliente(service.getClient(), service.getName(), professional.getName(), false);
            } catch (MessagingException e) {
                throw new RuntimeException("Erro ao enviar e-mail para o cliente", e);
            }
        }
    }



	   public void aceitarOuRecusarSolicitacao(Long serviceId, String professionalEmail, boolean aceitar) throws MessagingException {
		    // Buscar o serviço e o cliente
		    com.lgs.entities.Service service = serviceRepository.findById(serviceId)
		            .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

		    Professional prof = professionalRepository.findByEmail(professionalEmail)
		            .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

		    // Verificar o status do serviço
		    if (service.getStatus() != ServiceStatus.ABERTO) {
		        throw new RuntimeException("Somente serviços que estão em aberto podem ser aceitos ou recusados.");
		    }

		    // Lógica de aceitação ou recusa
		    if (aceitar) {
		        // O cliente aceita a solicitação
		        service.setStatus(ServiceStatus.INICIADO); // Alterar status para ACEITO
		        service.setInicioServico(LocalDate.now()); // Definir a data de início do serviço

		        // Marcar o profissional como não disponível
		        prof.solicitacoesParaCliente();
		        prof.setAvailable(false);
		        service.setProfessional(prof);
		        service.setProfessionalEmail(prof.getEmail());
		        service.setProfessionalName(prof.getName());
		        service.setServiceStatus(ProfessionalServiceStatus.ACEITO);
		        prof.addIdsSolicitacoesDeVinculacao(serviceId);
		        professionalRepository.save(prof);
		        serviceRepository.save(service);
		        // Enviar notificação para o profissional
		        try {
		            emailService.enviarNotificacaoParaProfissional2(prof, service);
		        } catch (MessagingException e) {
		            throw new RuntimeException("Erro ao enviar e-mail para o profissional", e);
		        }

		    } else {
		        // O cliente recusa a solicitação
		    	prof.solicitacoesParaCliente();
		        professionalRepository.save(prof);
		        service.setStatus(ServiceStatus.ABERTO); // Alterar status para RECUSADO
		        prof.setServiceStatus(ProfessionalServiceStatus.RECUSADO);
		        service.removerSolicitacaoVinculacao(prof.getId());
		        serviceRepository.save(service);
		        professionalRepository.save(prof);

		        try {
		            emailService.enviarNotificacaoParaProfissional(prof, service);
			        serviceRepository.save(service);
		        } catch (MessagingException e) {
		            throw new RuntimeException("Erro ao enviar e-mail para o profissional", e);
		        }
		    }

		}
   
	private void atualizarMediaAvaliacaoProfissional(Professional profissional) {
        List<com.lgs.entities.Service> servicos = serviceRepository.findByProfessional(profissional);

        double totalAvaliacao = servicos.stream()
                .flatMap(service -> service.getRatings().stream())
                .filter(rating -> rating.getProfessional() != null && rating.getRating() != null)
                .mapToDouble(Rating::getRating)
                .sum();

        long count = servicos.stream()
                .flatMap(service -> service.getRatings().stream())
                .filter(rating -> rating.getProfessional() != null && rating.getRating() != null)
                .count();

        profissional.setAverageRating(count > 0 ? totalAvaliacao / count : 0.0);
    }

	
	
	

    public List<com.lgs.entities.Service> listarServicosPorCliente(Long clientId) {
        return serviceRepository.findByClientId(clientId);
    }
    
    public List<com.lgs.entities.Service> listarTodosServicos() {
        return serviceRepository.findAll();
    }
    
 // Método para avaliar o cliente
    public void avaliarCliente(Long serviceId, String professionalEmail, Double rating, String comment) {
        // Passo 1: Obter o serviço e verificar se ele existe
        com.lgs.entities.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

        Professional prof = professionalRepository.findByEmail(professionalEmail).orElseThrow(() -> new RuntimeException("Profissional não encontrado!"));
        Client client = clientRepository.findById(service.getClient().getId()).orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));
        
        // Passo 2: Verificar se o profissional está vinculado ao serviço
        if (!service.getProfessional().getId().equals(prof.getId())) {
            throw new RuntimeException("Este profissional não está vinculado a este serviço.");
        }

        // Passo 3: Verificar se a avaliação já existe
        Optional<Rating> existingRating = service.getRatings().stream()
                .filter(r -> r.getProfessional().getId().equals(prof.getId()) && r.getClient().getId().equals(service.getClient().getId()))
                .findFirst();

        // Se já existir, atualizar a avaliação
        if (existingRating.isPresent()) {
            Rating ratingToUpdate = existingRating.get();
            ratingToUpdate.setRatedClient(rating);
            ratingToUpdate.setCommentaryForClient(comment);
            ratingRepository.save(ratingToUpdate);
            service.setRatedClient(rating);
            serviceRepository.save(service);
        } else {
            // Caso contrário, criar uma nova avaliação
            Professional professional = service.getProfessional();

            Rating newRating = new Rating();
            newRating.setRating(rating);
            newRating.setComment(comment);
            newRating.setClient(client);
            newRating.setProfessional(professional);
            newRating.setRatedClient(rating); // Avaliação feita pelo profissional sobre o cliente
            newRating.setService(service);
            service.setRatedClient(rating);

            ratingRepository.save(newRating);

            // Vincular a nova avaliação ao serviço
            service.getRatings().add(newRating);
            serviceRepository.save(service);
        }

       atualizarMediaAvaliacaoCliente(client);
        clientRepository.save(client);
    }

	private void atualizarMediaAvaliacaoCliente(Client client) {
        List<com.lgs.entities.Service> servicos = serviceRepository.findByClientId(client.getId());

        double totalAvaliacao = servicos.stream()
                .flatMap(service -> service.getRatings().stream())
                .filter(rating -> rating.getClient() != null && rating.getRatedClient() != null)
                .mapToDouble(Rating::getRatedClient)
                .sum();

        long count = servicos.stream()
                .flatMap(service -> service.getRatings().stream())
                .filter(rating -> rating.getProfessional() != null && rating.getRatedClient() != null)
                .count();

        client.setAverageRating(count > 0 ? totalAvaliacao / count : 0.0);
    }

	   public List<com.lgs.entities.Service> listarServicosPorEspecialidade(Long clientId, String specialty) {
	        return serviceRepository.findByClientIdAndSpecialty(clientId, specialty);
	    }
	   
	   public com.lgs.entities.Service solicitarServicoAoCliente(Long professionalId, Long  serviceId) throws MessagingException {
		    // Buscar o profissional e o cliente
		    Professional professional = professionalRepository.findById(professionalId)
		            .orElseThrow(() -> new RuntimeException("Profissional não encontrado"));

		    com.lgs.entities.Service service = serviceRepository.findById(serviceId)
		            .orElseThrow(() -> new RuntimeException("Serviço não encontrado"));

		    // Verificar se o profissional está disponível para criar uma solicitação
		    if (!professional.isAvailable()) {
		    	throw new RuntimeException("Você já está vinculado a um serviço em andamento. Finalize-o antes de solicitar um novo.");
		    }
		    	professional.addIdDeServicosQueDesejaFazer(serviceId);
		    // Associar o cliente e o profissional ao serviço
		    service.addIdsSolicitacoesDeVinculacao(professionalId);
        	service.addTotalProfessionalRequested();
        	   service.setProfessional(null);
               if(service.getServiceStatus() == ProfessionalServiceStatus.RECUSADO || service.getServiceStatus() == ProfessionalServiceStatus.ABERTO) {
               professional.setServiceStatus(ProfessionalServiceStatus.AGUARDANDO); // Define status como AGUARDANDO 
               }
		    // Salvar o serviço
               professionalRepository.save(professional);
		    serviceRepository.save(service);

		    // Enviar notificação por e-mail para o cliente
		    try {
		        emailService.enviarNotificacaoParaCliente2(service.getClient(), service.getName(), professional.getName(), false); // False pois é uma solicitação, não uma aceitação
		    } catch (MessagingException e) {
		        throw new RuntimeException("Erro ao enviar e-mail para o cliente", e);
		    }

		    return service;
		}

	  
}
