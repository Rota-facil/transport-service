package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RouteRepository extends JpaRepository<RouteEntity, UUID> {
    @Query("""
        SELECT r FROM RouteEntity r
        INNER JOIN r.trips t
        WHERE t.id = :tripId
    """)
    Optional<RouteEntity> findByTripId(@Param("tripId") UUID tripId);
}
