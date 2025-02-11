package com.lgs.dto;

import java.util.List;

public class SpecialtiesUpdateDto {
	
    private String specialties;
    private List<String> specialtiesToRemove;
    
	public String getSpecialties() {
		return specialties;
	}
	public void setSpecialties(String specialties) {
		this.specialties = specialties;
	}
	public List<String> getSpecialtiesToRemove() {
		return specialtiesToRemove;
	}
	public void setSpecialtiesToRemove(List<String> specialtiesToRemove) {
		this.specialtiesToRemove = specialtiesToRemove;
	}

    
    
}
