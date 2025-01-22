package com.lgs.dto;
public class ClientSummaryResponse {
    private String name;
    private int requestedServices;
    private int completedServices;
    private double averageRating;

    public ClientSummaryResponse(String name, int requestedServices, int completedServices, double averageRating) {
        this.name = name;
        this.requestedServices = requestedServices;
        this.completedServices = completedServices;
        this.averageRating = averageRating;
    }

    // Getters e Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRequestedServices() {
        return requestedServices;
    }

    public void setRequestedServices(int requestedServices) {
        this.requestedServices = requestedServices;
    }

    public int getCompletedServices() {
        return completedServices;
    }

    public void setCompletedServices(int completedServices) {
        this.completedServices = completedServices;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
