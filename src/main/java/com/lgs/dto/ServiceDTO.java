package com.lgs.dto;

public class ServiceDTO {
    private Long id;
    private String name;
    private String description;
    private String location;
    private String specialty;

    public ServiceDTO(Long id, String name, String description, String location, String specialty) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.specialty = specialty;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

    
    
}
