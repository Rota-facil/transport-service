package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface BoardPointRepository extends JpaRepository<BoardPointEntity, UUID> {
    @Query("""
        SELECT b FROM BoardPointEntity b
        WHERE b.id IN (:boardPointsIds)
    """)
    Set<BoardPointEntity> findAllSetById(@Param("boardPointsIds") Set<UUID> boardPointsIds);

    @Query("""
        SELECT b FROM BoardPointEntity b
        WHERE b.id IN (:boardPointsIds)
    """)
    Map<UUID, BoardPointEntity> findAllMapById(@Param("boardPointsIds") List<UUID> boardPointsIds);
}
