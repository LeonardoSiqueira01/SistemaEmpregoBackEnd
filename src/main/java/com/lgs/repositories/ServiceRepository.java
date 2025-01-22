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

    List<Service> findByClientEmail(String email);
    
    List<Service> findBySpecialtyAndClientEmail(String specialty, String email);
    
    List<Service> findByStatus(Service.ServiceStatus status);
    
    List<Service> findByClientId(Long clientId);
    int countByClientEmail(String email); // Contar serviços pelo email do cliente



    int countByClientEmailAndStatus(String email, Service.ServiceStatus status); // Contar serviços por email e status

    @Query("SELECT COUNT(s) FROM Service s WHERE s.client.email = :email AND s.status = :status")
    long countServicesByClientEmailAndStatus(@Param("email") String email, @Param("status") Service.ServiceStatus status);

    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.service.client.email = :email")
    Double calculateAverageRatingByUserEmail(@Param("email") String email); // Calcular média de avaliações pelo email do cliente

    List<Service> findByProfessional(Professional professional); 
    boolean existsByProfessionalAndStatusNot(Professional professional, ServiceStatus status);


}

