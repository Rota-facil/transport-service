package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.entities.BoardPointRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardPointRouteRepository extends JpaRepository<BoardPointRouteEntity, UUID> {
    @Query(value = """
        SELECT b.* FROM board_points_routes_tb br
        INNER JOIN board_points_tb b USING(board_point_id)
        INNER JOIN routes_tb r USING(route_id)
        INNER JOIN trips_tb t USING(route_id)
        WHERE b.board_point_id = :boardPointId
        AND t.trip_id = :tripId
    """, nativeQuery = true)
    Optional<BoardPointEntity> findByIdAndTripId(@Param("boardPointId") UUID boardPointId, @Param("tripId") UUID tripId);
}
