package com.lgs.dto;

import java.util.List;

public class ClientDTOProfile {
    private Long id;
    private String name;
    private String email;
    private String type; // Sempre será "CLIENT"
    private Integer totalServicesRequested;
    private Integer totalServicesCompleted;
    private Double averageRating;
    private List<RatingDtoForClient> ratings;

    // Construtores, Getters e Setters

    public ClientDTOProfile(Long id, String name, String email, String type, 
                            Integer totalServicesRequested, Integer totalServicesCompleted,
                            Double averageRating, List<RatingDtoForClient> ratings) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.type = type;
        this.totalServicesRequested = totalServicesRequested;
        this.totalServicesCompleted = totalServicesCompleted;
        this.averageRating = averageRating;
        this.ratings = ratings;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getTotalServicesRequested() {
		return totalServicesRequested;
	}

	public void setTotalServicesRequested(Integer totalServicesRequested) {
		this.totalServicesRequested = totalServicesRequested;
	}

	public Integer getTotalServicesCompleted() {
		return totalServicesCompleted;
	}

	public void setTotalServicesCompleted(Integer totalServicesCompleted) {
		this.totalServicesCompleted = totalServicesCompleted;
	}

	public Double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Double averageRating) {
		this.averageRating = averageRating;
	}

	public List<RatingDtoForClient> getRatings() {
		return ratings;
	}

	public void setRatings(List<RatingDtoForClient> ratings) {
		this.ratings = ratings;
	}

}
