package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.TripStatusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripStatusRepository extends JpaRepository<TripStatusEntity, UUID> {
    @Query("""
        SELECT COUNT(ts) > 0 FROM TripStatusEntity ts
        INNER JOIN ts.trip t
        WHERE t.id = :tripId
        AND ts.progress IN (
                com.rota.facil.transport_service.domain.enums.Progress.STARTED,
                com.rota.facil.transport_service.domain.enums.Progress.CANCELLED
            )
    """)
    boolean isTripInitOrCancelled(@Param("tripId") UUID tripId);

    @Query("""
        SELECT COUNT(ts) > 0 FROM TripStatusEntity ts
        INNER JOIN ts.trip t
        WHERE t.id = :tripId
        AND ts.progress IN (com.rota.facil.transport_service.domain.enums.Progress.CANCELLED)
    """)
    boolean isTripCancelled(@Param("tripId") UUID tripId);
}
