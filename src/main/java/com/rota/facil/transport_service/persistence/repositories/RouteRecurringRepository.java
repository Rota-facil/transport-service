package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import com.rota.facil.transport_service.persistence.entities.RouteRecurringEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RouteRecurringRepository extends JpaRepository<RouteRecurringEntity, UUID> {
    boolean existsByRoute_IdAndBus_Id(UUID routeId, UUID busId);

    @Query("""
        SELECT rr FROM RouteRecurringEntity rr
        INNER JOIN rr.route r
        WHERE :daysOfWeek MEMBER OF r.daysOfWeek
        AND r.active = true
    """)
    List<RouteRecurringEntity> findAllRouteRecurringToday(@Param("daysOfWeek") DaysOfWeek daysOfWeek);

    void deleteAllByRoute_Id(UUID routeId);

    void deleteAllByBus_Id(UUID busId);
}
