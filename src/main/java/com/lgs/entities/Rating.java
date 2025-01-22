package com.lgs.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double rating;  // Nota da avaliação
    private String comment; // Comentário da avaliação

    @JsonIgnore
@ManyToOne
    @JsonBackReference  // Evita a serialização recursiva do lado do cliente
    @JoinColumn(name = "client_id")
    private Client client; // Cliente que fez a avaliação (opcional, pode ser removido)

    @JsonIgnore
@ManyToOne
    @JsonBackReference
    @JoinColumn(name = "professional_id")
    private Professional professional; // Profissional avaliado

    private Double ratedClient; // Cliente avaliado pelo profissional

    
    private Double ratedProfessional; // Cliente avaliado pelo profissional
private String commentaryForClient;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "service_id")
    private Service service; // O serviço associado à avaliação

    
    @Column(name = "dtype")
    private String dtype; // Novo campo
    
    public Rating(Long id, Double rating, String comment, Client client, Professional professional, Double ratedClient,Double ratedProfessional, 
			Service service) {
		this.id = id;
		this.rating = rating;
		this.comment = comment;
		this.client = client;
		this.professional = professional;
		this.service = service;
		this.dtype = "Rating";
		this.ratedClient = ratedClient;
		this.ratedProfessional = ratedProfessional;
	}
    

    public String getCommentaryForClient() {
		return commentaryForClient;
	}


	public void setCommentaryForClient(String commentaryForClient) {
		this.commentaryForClient = commentaryForClient;
	}


	public Rating() {
        this.dtype = "Rating";  // Exemplo de valor padrão
    }

	

	public Double getRatedClient() {
		return ratedClient;
	}


	public void setRatedClient(Double ratedClient) {
		this.ratedClient = ratedClient;
	}


	public Double getRatedProfessional() {
		return ratedProfessional;
	}


	public void setRatedProfessional(Double ratedProfessional) {
		this.ratedProfessional = ratedProfessional;
	}


	// Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
    }


    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }
}
 
