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
    @Query("""
        SELECT rr FROM RouteRecurringEntity rr
        WHERE :daysOfWeek MEMBER OF rr.daysOfWeek
    """)
    List<RouteRecurringEntity> findAllRouteRecurringToday(@Param("daysOfWeek") DaysOfWeek daysOfWeek);
}
