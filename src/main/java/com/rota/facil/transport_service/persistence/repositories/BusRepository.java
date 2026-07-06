package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.BusEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BusRepository extends JpaRepository<BusEntity, UUID> {
    @Query("""
        SELECT COUNT(b) FROM BusEntity b
        WHERE b.prefectureId = :prefectureId
    """)
    Long countByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT b FROM BusEntity b
        WHERE b.id = :busId
        AND b.prefectureId = :prefectureId
    """)
    Optional<BusEntity> findByIdAndPrefectureId(@Param("busId") UUID busId, @Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT b FROM BusEntity b
        WHERE b.prefectureId = :prefectureId
    """)
    List<BusEntity> findAllByPrefectureId(@Param("prefectureId)") UUID prefectureId);
}
