package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.TripUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TripUserRepository extends JpaRepository<TripUserEntity, UUID> {
    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END
        FROM TripUserEntity tu
        INNER JOIN tu.trip t
        INNER JOIN tu.user u
        WHERE t.id = :tripId
        AND u.id = :userId
    """)
    boolean existsByTripIdAndUserId(@Param("tripId") UUID tripId, @Param("userId") UUID userId);
}
