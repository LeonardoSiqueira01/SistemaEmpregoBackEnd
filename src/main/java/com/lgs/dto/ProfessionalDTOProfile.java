package com.lgs.dto;

import java.util.List;

public class ProfessionalDTOProfile {
	    private Long id;
	    private String name;
	    private String email;
	    private String type; // Sempre será "CLIENT"
	    private Integer totalServicesRequested; //o quanto chamaram ele pra fazer servicos
	    private Integer totalServicesCompleted;
	    private Double averageRating;
	    private String specialties; 
	    private String location; //Endereço do Profissional
	    private List<RatingDtoForProfessional> ratings;
	    // Construtores, Getters e Setters

	    public ProfessionalDTOProfile(Long id, String name, String email, String type, 
	                            Integer totalServicesRequested, Integer totalServicesCompleted,
	                            Double averageRating,String specialties,String location,  List<RatingDtoForProfessional> ratings) {
	        this.id = id;
	        this.name = name;
	        this.email = email;
	        this.type = type;
	        this.totalServicesRequested = totalServicesRequested;
	        this.totalServicesCompleted = totalServicesCompleted;
	        this.averageRating = averageRating;
	        this.ratings = ratings;
	        this.location = location;
	        this.specialties = specialties;
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

		public List<RatingDtoForProfessional> getRatings() {
			return ratings;
		}

		public void setRatings(List<RatingDtoForProfessional> ratings) {
			this.ratings = ratings;
		}

		public String getSpecialties() {
			return specialties;
		}

		public void setSpecialties(String specialties) {
			this.specialties = specialties;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}

	

}
