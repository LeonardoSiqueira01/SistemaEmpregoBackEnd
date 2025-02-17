package com.lgs.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ProfessionalDtoPerfil {

    private String name;
    private String email;
    private String location;
    private String specialtiesToRemove; // Para remover especialidades
    private String specialties; // Para adicionar/atualizar especialidades

    public ProfessionalDtoPerfil() {
    }

    @JsonCreator
    public ProfessionalDtoPerfil(@JsonProperty("name") String name,
                                 @JsonProperty("email") String email,
                                 @JsonProperty("location") String location,
                                 @JsonProperty("specialties") String specialties,
                                 @JsonProperty("specialtiesToRemove") String specialtiesToRemove) {
        this.name = name;
        this.email = email;
        this.specialties = specialties;
        this.location = location;
        this.specialtiesToRemove = specialtiesToRemove;
    }

    // Getters and Setters
    public String getSpecialtiesToRemove() {
        return specialtiesToRemove;
    }

    public void setSpecialtiesToRemove(String specialtiesToRemove) {
        this.specialtiesToRemove = specialtiesToRemove;
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
