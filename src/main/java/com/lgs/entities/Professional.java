package com.lgs.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Set;

import com.lgs.dto.ProfessionalServiceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;

@Entity
public class Professional extends User {

    private Integer totalServicesCompleted = 0;
    private Double averageRating = 0.0;
    private boolean available =true;
    private Integer totalServicesRequested = 0; 
    private Integer servicosQueSolicitouParticipacao = 0;
    
    @Column(name = "specialties", nullable = true)
    private String specialties;
    
    private String location;

    @OneToMany(mappedBy = "professional")
    private List<Service> services = new ArrayList<>();
    

    @OneToMany(mappedBy = "professional")
    private List<Rating> ratings = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProfessionalServiceStatus serviceStatus = ProfessionalServiceStatus.ABERTO;
    
    private Set<Long> solicitacoesVinculacaoServico = new HashSet<>();

    
    public Professional() {
        setType("PROFESSIONAL");
        this.solicitacoesVinculacaoServico = new HashSet<>();

    }
    
    public Integer getTotalServicesRequested() {
        return totalServicesRequested;
    }
    
    public void incrementTotalServicesCompleted() {
        this.totalServicesCompleted++;
    }

    public void updateAverageRating() {
        OptionalDouble average = ratings.stream()
                                        .mapToDouble(Rating::getRating)
                                        .average();
        this.averageRating = average.orElse(0.0);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTotalServicesCompleted() {
        return totalServicesCompleted;
    }

    public void setTotalServicesCompleted(Integer totalServicesCompleted) {
        this.totalServicesCompleted = totalServicesCompleted;
    }
    
    public String getSpecialties() {
		return specialties;
	}

	public void setSpecialties(String specialties) {
		this.specialties = specialties;
	}

	public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services != null ? services : new ArrayList<>();
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings != null ? ratings : new ArrayList<>();
    }

    public ProfessionalServiceStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(ProfessionalServiceStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

	public Set<Long> getSolicitacoesVinculacaoServico() {
		return solicitacoesVinculacaoServico;
	}

    public void addIdsSolicitacoesDeVinculacao(Long idServico) {
        if (solicitacoesVinculacaoServico == null) {
            solicitacoesVinculacaoServico = new HashSet<>(); // Garantir inicialização se for null
        }
        solicitacoesVinculacaoServico.add(idServico);
    }
    
    public void removeIdSolicitacaoDeVinculacao(Long idServico) {
        if (solicitacoesVinculacaoServico != null && solicitacoesVinculacaoServico.contains(idServico)) {
            solicitacoesVinculacaoServico.remove(idServico);
        }
    }

    public void addTotalServicesRequested() {
        this.totalServicesRequested++;
    }

    public void solicitacoesParaCliente( ) {
    	servicosQueSolicitouParticipacao = servicosQueSolicitouParticipacao+ 1;
    }

	public Integer getServicosQueSolicitouParticipacao() {
		return servicosQueSolicitouParticipacao;
	}
    

    public void addSpecialty(String specialty) {
        if (this.specialties == null || this.specialties.isEmpty()) {
            this.specialties = specialty;
        } else {
            // Adiciona a nova especialidade com vírgula
            if (!this.specialties.contains(specialty)) {
                this.specialties += "; " + specialty;
            }
        }
    }

    
    public void removeSpecialty(String specialty) {
        if (this.specialties != null && !this.specialties.isEmpty()) {
            String[] specialtiesArray = this.specialties.split("; ");
            StringBuilder newSpecialties = new StringBuilder();

            for (String s : specialtiesArray) {
                if (!s.equalsIgnoreCase(specialty)) {
                    if (newSpecialties.length() > 0) {
                        newSpecialties.append("; ");
                    }
                    newSpecialties.append(s);
                }
            }

            this.specialties = newSpecialties.toString();
        }
    }

    
}
