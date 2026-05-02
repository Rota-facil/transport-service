package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.entities.InstitutionEntity;
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
        INNER JOIN t.tripStatus ts
        WHERE t.id = :tripId
        AND ts.progress NOT IN (com.rota.facil.transport_service.domain.enums.Progress.CANCELLED)
    """)
    Optional<RouteEntity> findByTripId(@Param("tripId") UUID tripId);

    @Query(value = """
        SELECT b.* FROM routes_tb r
        INNER JOIN trips_tb t USING(route_id)
        INNER JOIN board_points_routes_tb br USING(route_id)
        INNER JOIN board_points b USING(board_point_id)
        WHERE t.trip_id = :tripId
        AND ST_DWithin(b.geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, 10)
    """, nativeQuery = true)
    Optional<BoardPointEntity> findBoardPointByTripIdAndCoordinates(@Param("tripId") UUID tripId, @Param("longitude") double longitude, @Param("latitude") double latitude);

    @Query(value = """
        SELECT i.* FROM routes_tb r
        INNER JOIN trips_tb t USING(route_id)
        INNER JOIN routes_institutions_tb ri USING(route_id)
        INNER JOIN institutions_tb i USING(institution_id)
        WHERE t.trip_id = :tripId
        AND ST_DWithin(i.geom, ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography, 10)
    """, nativeQuery = true)
    Optional<InstitutionEntity> findInstitutionByTripIdAndCoordinates(@Param("tripId") UUID tripId, @Param("longitude") double longitude, @Param("latitude") double latitude);
}
