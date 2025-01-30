package com.lgs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lgs.entities.Professional;
import com.lgs.entities.Service;
import com.lgs.entities.Service.ServiceStatus;

@EnableJpaRepositories
@Repository 
public interface ServiceRepository extends JpaRepository<Service, Long> {

	@Query("SELECT s FROM Service s WHERE s.client.email = :email")
    List<Service> findByClientEmail(@Param("email") String email);
	
	@Query("SELECT s FROM Service s WHERE s.specialty = :specialty AND s.client.email = :email")
    List<Service> findBySpecialtyAndClientEmail(@Param("specialty") String specialty, @Param("email") String email);    
    List<Service> findByStatus(Service.ServiceStatus status);
    
    List<Service> findByClientId(Long clientId);
    @Query("SELECT COUNT(s) FROM Service s WHERE s.client.email = :email")
    int countByClientEmail(@Param("email") String email);



    @Query("SELECT COUNT(s) FROM Service s WHERE s.client.email = :email AND s.status = :status")
    int countByClientEmailAndStatus(@Param("email") String email, @Param("status") ServiceStatus status);
    @Query("SELECT COUNT(s) FROM Service s WHERE s.client.email = :email AND s.status = :status")
    long countServicesByClientEmailAndStatus(@Param("email") String email, @Param("status") Service.ServiceStatus status);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.service.client.email = :email")
    Double calculateAverageRatingByUserEmail(@Param("email") String email); // Calcular média de avaliações pelo email do cliente

    List<Service> findByProfessional(Professional professional); 
    boolean existsByProfessionalAndStatusNot(Professional professional, ServiceStatus status);

    List<Service> findByClientIdAndSpecialty(Long clientId, String specialty);

}

