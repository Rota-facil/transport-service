package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.FeedBackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FeedBackRepository extends JpaRepository<FeedBackEntity, UUID> {
    @Query("""
        SELECT AVG(f.note) FROM FeedBackEntity f
        INNER JOIN f.receiver r
        WHERE r.id = :userToEvaluateId
    """)
    double calculateMediaNoteByUserId(@Param("userToEvaluateId") UUID userToEvaluateId);
}
