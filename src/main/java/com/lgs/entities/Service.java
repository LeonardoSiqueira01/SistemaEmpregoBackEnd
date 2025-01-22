package com.lgs.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.lgs.dto.ProfessionalServiceStatus;

@Entity
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
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


    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "professional_id")
    private Professional professional;

    private String professionalName;
    private String professionalEmail;

    @Enumerated(EnumType.STRING)
    private ServiceStatus status;
     
    private String specialty;


    @OneToMany(mappedBy = "service" )
    private List<Rating> ratings; // Avaliações associadas ao serviço
   
    public enum ServiceStatus {
        ABERTO, INICIADO, FINALIZADO, CANCELADO
    }
    
    @Enumerated(EnumType.STRING)
    private ProfessionalServiceStatus serviceStatus = ProfessionalServiceStatus.ABERTO;

    // Getters e Setters
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

	
	
	public String getProfessionalName() {
		return professionalName;
	}


	public void setProfessionalName(String name) {
	    this.professionalName = name;
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
}
