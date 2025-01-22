package com.lgs.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lgs.entities.Client;
import com.lgs.entities.Professional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {

	  Optional<Professional> findByEmail(String email);
	    Optional<Professional> findById(Long id);

}
