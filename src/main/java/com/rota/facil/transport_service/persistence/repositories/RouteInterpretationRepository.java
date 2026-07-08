package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.RouteInterpretationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteInterpretationRepository extends JpaRepository<RouteInterpretationEntity, UUID> {
    @Query("""
        SELECT ri FROM RouteInterpretationEntity ri
        INNER JOIN ri.route r
        WHERE r.id = :routeId
        AND r.prefectureId = :prefectureId
        AND r.active = true
        ORDER BY ri.createdAt DESC
    """)
    List<RouteInterpretationEntity> findAllByRouteIdAndPrefectureId(@Param("routeId") UUID routeId, @Param("prefectureId") UUID prefectureId);
}
