package com.lgs.dto;


public class ProfessionalDTO {

    private Long id;
    private String name;
    private String email;
    private Integer totalServicesCompleted;
    private Double averageRating;
    private boolean available;
    private String specialties;
    private String location;

    // Construtor
    public ProfessionalDTO(Long id, String name, String email, Integer totalServicesCompleted, 
                           Double averageRating, boolean available, String specialties, String location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.totalServicesCompleted = totalServicesCompleted;
        this.averageRating = averageRating;
        this.available = available;
        this.specialties = specialties;
        this.location = location;
    }

    // Getters e Setters
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
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
