package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.entities.BoardPointVisitedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardPointVisitedRepository extends JpaRepository<BoardPointVisitedEntity, UUID> {
    @Query("""
        SELECT bpv FROM BoardPointVisitedEntity bpv
        INNER JOIN bpv.boardPoint b
        INNER JOIN bpv.trip t
        WHERE b.id = :boardPointId
        AND t.id = :tripId
    """)
    Optional<BoardPointVisitedEntity> findByBoardPointIdAndTripId(@Param("boardPointId") UUID boardPointId, @Param("tripId") UUID tripId);

    @Query("""
        SELECT bpv FROM BoardPointVisitedEntity bpv
        INNER JOIN bpv.trip t
        WHERE t.id = :tripId
        AND bpv.going IS TRUE
        AND bpv.return_ IS TRUE
    """)
    List<BoardPointVisitedEntity> findReturnByTripId(@Param("tripId") UUID tripId);
}
