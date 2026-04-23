package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.TripEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, UUID> {
    @Query("""
        SELECT t FROM TripEntity t
        INNER JOIN t.bus b
        INNER JOIN b.driver d
        WHERE t.id = :tripId
        AND d.id = :driverId
    """)
    Optional<TripEntity> findByIdAndDriverId(@Param("tripId") UUID tripId, @Param("driverId") UUID driverId);
}
