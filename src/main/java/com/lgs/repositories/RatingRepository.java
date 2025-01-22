package com.lgs.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lgs.entities.Rating;
import com.lgs.entities.Professional;
import com.lgs.entities.Client;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // Buscar avaliações feitas por um cliente
    List<Rating> findByClient(Client client);

    // Buscar avaliações de um profissional
    List<Rating> findByProfessional(Professional professional);

    // Buscar avaliações relacionadas a um cliente avaliado
    List<Rating> findByRatedClient(Client ratedClient);
}
