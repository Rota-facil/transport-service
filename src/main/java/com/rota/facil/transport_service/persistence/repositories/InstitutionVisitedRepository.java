package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.InstitutionVisitedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstitutionVisitedRepository extends JpaRepository<InstitutionVisitedEntity, UUID> {
    @Query("""
        SELECT iv FROM InstitutionVisitedEntity iv
        INNER JOIN iv.institution i
        INNER JOIN iv.trip t
        WHERE i.id = :institutionId
        AND t.id = :tripId
    """)
    Optional<InstitutionVisitedEntity> findByInstitutionIdAndTripId(@Param("institutionId") UUID institutionId, @Param("tripId") UUID tripId);

    @Query("""
        SELECT iv FROM InstitutionVisitedEntity iv
        INNER JOIN iv.trip t
        WHERE t.id = :tripId
    """)
    List<InstitutionVisitedEntity> findByTripId(@Param("tripId") UUID tripId);

    @Query("""
        SELECT iv FROM InstitutionVisitedEntity iv
        INNER JOIN iv.trip t
        WHERE t.id = :tripId
        AND iv.going IS TRUE
        AND iv.return_ IS FALSE
    """)
    List<InstitutionVisitedEntity> findGoingByTripId(@Param("tripId") UUID tripId);

    @Query("""
        SELECT iv FROM InstitutionVisitedEntity iv
        INNER JOIN iv.trip t
        WHERE t.id = :tripId
        AND iv.return_ IS TRUE
        AND iv.going IS TRUE
    """)
    List<InstitutionVisitedEntity> findReturnByTripId(@Param("tripId") UUID tripId);
}
