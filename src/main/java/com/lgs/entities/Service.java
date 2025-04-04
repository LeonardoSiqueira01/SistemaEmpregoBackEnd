package com.lgs.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lgs.dto.ProfessionalServiceStatus;
import com.lgs.repositories.ProfessionalRepository;

@Entity
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate serviceDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inicioServico;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate conclusaoServico;
     
   
    private String location; 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // Evita a serialização recursiva do cliente
    private Client client;
    
    private List<Long> solicitacoesVinculacaoProfessional = new ArrayList<>();

    private Double ratedClient = 0.0; 

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professional_id")
    private Professional professional;

    private String professionalName;
    private String professionalEmail;
    private String ClientName;
    private String ClientEmail;
    private Integer totalProfessionalRequested = 0; 

    @Enumerated(EnumType.STRING)
    private ServiceStatus status;
     
    private String specialty;
    
    private Set<Long> solicitacoesVinculacaoServico = new HashSet<>();


    @OneToMany(mappedBy = "service" )
    private List<Rating> ratings; // Avaliações associadas ao serviço
   
    public enum ServiceStatus {
        ABERTO, INICIADO, FINALIZADO, CANCELADO
    }
    
    @Enumerated(EnumType.STRING)
    private ProfessionalServiceStatus serviceStatus = ProfessionalServiceStatus.ABERTO;

    public Service() {
        solicitacoesVinculacaoServico = new HashSet<>();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	
	public Double getRatedClient() {
		return ratedClient;
	}

	public void setRatedClient(Double ratedClient) {
		this.ratedClient = ratedClient;
	}

	public String getProfessionalName() {
		return professionalName;
	}


	public void setProfessionalName(String name) {
	    this.professionalName = name;
	}
    
	public Set<Long> getSolicitacoesVinculacaoServico() {
	    return solicitacoesVinculacaoServico;
	}

	public String getProfessionalEmail() {
		return professionalEmail;
	}

	public void setProfessionalEmail(String professionalEmail) {
		this.professionalEmail = professionalEmail;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public LocalDate getInicioServico() {
		return inicioServico;
	}

	public void setInicioServico(LocalDate inicioServico) {
		this.inicioServico = inicioServico;
	}

	public LocalDate getConclusaoServico() {
		return conclusaoServico;
	}

	public void setConclusaoServico(LocalDate conclusaoServico) {
		this.conclusaoServico = conclusaoServico;
	}

	public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Professional getProfessional() {
        return professional;
    }

    public void setProfessional(Professional professional) {
        this.professional = professional;
        if (professional != null) {
            this.professionalName = professional.getName();
        }
    }


    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public ProfessionalServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ProfessionalServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
    

	public List<Long> getSolicitacoesVinculacaoProfessional() {
		return solicitacoesVinculacaoProfessional;
	}

	public void IdsSolicitacoesDeVinculacao(Long idProfessional) {
		this.solicitacoesVinculacaoProfessional.add(idProfessional);
	}
	
	   public void addIdsSolicitacoesDeVinculacao(Long idServico) {
	        solicitacoesVinculacaoServico.add(idServico);
	    }
	   public void removeIdSolicitacaoProfissional(Long idProfissional) {
		    solicitacoesVinculacaoProfessional.remove(idProfissional);
		}

	    public void addTotalProfessionalRequested() {
	        this.totalProfessionalRequested = totalProfessionalRequested+1;
	    }

	    
	    public List<Professional> listarProfissionaisVinculados(ProfessionalRepository professionalRepository) {
	
	        return solicitacoesVinculacaoServico.stream()
	                .map(id -> professionalRepository.findById(id).orElse(null)) // Procura o profissional pelo ID
	                .filter(prof -> prof != null) // Filtra para não retornar nulo
	                .collect(Collectors.toList()); // Retorna uma lista de profissionais
	    }
	    
	    public void removerSolicitacaoVinculacao(Long idServico) {
	        if (this.solicitacoesVinculacaoServico != null && solicitacoesVinculacaoServico.contains(idServico)) {
	            solicitacoesVinculacaoServico.remove(idServico);
	        }
	    }

		public String getClientName() {
			return ClientName;
		}

		public void setClientName(String clientName) {
			ClientName = clientName;
		}

		public String getClientEmail() {
			return ClientEmail;
		}

		public void setClientEmail(String clientEmail) {
			ClientEmail = clientEmail;
		}

	    
	    
}
