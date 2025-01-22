package com.lgs.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Client extends User {

    private Integer totalServicesRequested = 0; 
    private Double averageRating = 0.0; 
    private Integer totalServicesCompleted = 0;

    @JsonBackReference // Impede a serialização recursiva
    @OneToMany(mappedBy = "client")
    private List<Service> services = new ArrayList<>();

    @OneToMany(mappedBy = "client")
    private List<Rating> ratings = new ArrayList<>();  // Avaliações feitas pelo cliente
    
    // Construtor com parâmetros, usado para deserialização JSON
    @JsonCreator
    public Client(
        @JsonProperty("id") Long id,
        @JsonProperty("name") String name,
        @JsonProperty("totalServicesRequested") Integer totalServicesRequested,
        @JsonProperty("averageRating") Double averageRating
    ) {
        super();
        this.setId(id);
        this.setName(name);
        this.totalServicesRequested = totalServicesRequested;
        this.averageRating = averageRating;
        setType("CLIENT");  
    }

    public Client() {
        setType("CLIENT");
    }

	// Método para incrementar total de serviços solicitados
    public void incrementTotalServicesRequested() {
        this.totalServicesRequested++;
    }

    // Getters e Setters
    public Integer getTotalServicesRequested() {
        return totalServicesRequested;
    }

    public void setTotalServicesRequested(Integer totalServicesRequested) {
        this.totalServicesRequested = totalServicesRequested;
    }

    public Double getAverageRating() {
        return averageRating;
    }

   




    // Getter para serviços solicitados
    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    // Getter para as avaliações feitas pelo cliente
    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }
    public void incrementTotalServicesCompleted() {
        this.totalServicesCompleted++;
    }

    public void updateAverageRating() {
        OptionalDouble average = ratings.stream()
                                        .mapToDouble(Rating::getRatedClient)
                                        .average();
        this.averageRating = average.orElse(0.0);
    }

	public Integer getTotalServicesCompleted() {
		return totalServicesCompleted;
	}

	 public void setAverageRating(Double averageRating) {
	        this.averageRating = averageRating;
	    }
    

  
}
