package com.lgs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lgs.entities.Rating;

@Repository
public interface EvaluationRepository extends JpaRepository<Rating, Long> {

    // Consulta para calcular a média de avaliação de um usuário
    @Query("SELECT AVG(r.rating) FROM Rating r WHERE r.client.id = :userId")
    Double calculateAverageRatingByUserId(Long userId);
}
