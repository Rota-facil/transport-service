package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.TripEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;

@Repository
public interface TripRepository extends JpaRepository<TripEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TripEntity t WHERE t.id = :tripId")
    Optional<TripEntity> findByIdForUpdate(@Param("tripId") UUID tripId);

    @Query("""
        SELECT t FROM TripEntity t
        INNER JOIN t.bus b
        INNER JOIN b.driver d
        WHERE t.id = :tripId
        AND d.id = :driverId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TripEntity> findByIdAndDriverId(@Param("tripId") UUID tripId, @Param("driverId") UUID driverId);

    @Query("""
        SELECT t FROM TripEntity t
        INNER JOIN t.route r
        WHERE r.id = :routeId
    """)
    List<TripEntity> findAllByRouteId(@Param("routeId") UUID routeId);

    @Query("""
        SELECT t FROM TripEntity t
        INNER JOIN t.route r
        INNER JOIN t.tripStatus ts
        WHERE r.id = :routeId
        AND ts.progress IN (
                com.rota.facil.transport_service.domain.enums.Progress.RETURN_FINISHED,
                com.rota.facil.transport_service.domain.enums.Progress.CANCELLED
            )
    """)
    List<TripEntity> findAllFinishedByRouteId(@Param("routeId") UUID routeId);

    @Query("""
        SELECT t FROM TripEntity t
        INNER JOIN t.tripStatus ts
        WHERE t.id = :tripId
        AND ts.progress NOT IN (com.rota.facil.transport_service.domain.enums.Progress.STARTED)
    """)
    Optional<TripEntity> findNotStartedById(@Param("tripId") UUID tripId);

    @Query("""
        SELECT t FROM TripEntity t
        INNER JOIN t.tripStatus ts
        WHERE t.id = :tripId
        AND t.prefectureId = :prefectureId
        AND ts.progress NOT IN (com.rota.facil.transport_service.domain.enums.Progress.STARTED)
    """)
    Optional<TripEntity> findNotStartedByIdAndPrefectureId(@Param("tripId") UUID tripId, @Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT t FROM TripEntity t
        WHERE t.id = :tripId
        AND  t.prefectureId = :prefectureId
    """)
    Optional<TripEntity> findByIdAndPrefectureId(@Param("tripId") UUID tripId, @Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT t FROM TripEntity t
        WHERE t.prefectureId = :prefectureId
    """)
    List<TripEntity> findAllByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT t FROM TripEntity t
        INNER JOIN t.bus b
        INNER JOIN b.driver d
        WHERE d.id = :driverId
        AND t.createdAt = CURRENT_DATE
    """)
    List<TripEntity> findAllTodayByDriverId(@Param("driverId") UUID driverId);

    @Query("""
        SELECT t FROM TripEntity t
        WHERE t.prefectureId = :prefectureId
        AND t.createdAt = CURRENT_DATE
    """)
    Page<TripEntity> findAllByPrefectureIdToday(@Param("prefectureId") UUID prefectureId, Pageable pageable);

    @Query("""
        SELECT COUNT(t) FROM TripEntity t
        INNER JOIN t.route r
        WHERE r.prefectureId = :prefectureId
        AND CAST(t.createdAt AS DATE) = CURRENT_DATE
    """)
    Long countTodayByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT COUNT(t) FROM TripEntity t
        INNER JOIN t.route r
        WHERE r.prefectureId = :prefectureId
        AND t.createdAt = CURRENT_DATE
        AND t.actualStatus = com.rota.facil.transport_service.domain.enums.Progress.CANCELLED
    """)
    Long countCancelledByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
    SELECT COUNT(t)
    FROM TripEntity t
    INNER JOIN t.route r
    WHERE r.prefectureId = :prefectureId
    AND t.createdAt = CURRENT_DATE
    AND t.actualStatus NOT IN (
        com.rota.facil.transport_service.domain.enums.Progress.NOT_STARTED,
        com.rota.facil.transport_service.domain.enums.Progress.CANCELLED,
        com.rota.facil.transport_service.domain.enums.Progress.RETURN_FINISHED
    )
""")
    Long countStartedTodayByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
    SELECT COUNT(t)
    FROM TripEntity t
    INNER JOIN t.route r
    INNER JOIN t.tripStatus ts
    WHERE r.prefectureId = :prefectureId
    AND t.createdAt = CURRENT_DATE
    AND ts.progress = com.rota.facil.transport_service.domain.enums.Progress.STARTED
    AND ts.delay = com.rota.facil.transport_service.domain.enums.Delay.PUNCTUAL
""")
    Long countStartedPunctualTodayByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT SUM(t.students) FROM TripEntity t
        INNER JOIN t.route r
        INNER JOIN t.tripStatus ts
        WHERE r.prefectureId = :prefectureId
        AND t.createdAt = CURRENT_DATE
        AND ts.progress = com.rota.facil.transport_service.domain.enums.Progress.STARTED
    """)
    Long countStudentsServedByPrefectureId(@Param("prefectureId") UUID prefectureId);
    @Query("""
        SELECT COUNT(t)
        FROM TripEntity t
        INNER JOIN t.route r
        WHERE r.prefectureId = :prefectureId
        AND t.createdAt = CURRENT_DATE
        AND t.actualStatus NOT IN (
            com.rota.facil.transport_service.domain.enums.Progress.NOT_STARTED,
            com.rota.facil.transport_service.domain.enums.Progress.CANCELLED,
            com.rota.facil.transport_service.domain.enums.Progress.RETURN_FINISHED
        )
    """)
    Long countInRouteTodayByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT COUNT(t)
        FROM TripEntity t
        INNER JOIN t.route r
        WHERE r.prefectureId = :prefectureId
        AND t.createdAt = CURRENT_DATE
        AND t.actualStatus = com.rota.facil.transport_service.domain.enums.Progress.RETURN_FINISHED
    """)
    Long countFinishedTodayByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT COUNT(t)
        FROM TripEntity t
        INNER JOIN t.route r
        WHERE r.prefectureId = :prefectureId
        AND t.createdAt = CURRENT_DATE
        AND t.actualStatus = com.rota.facil.transport_service.domain.enums.Progress.NOT_STARTED
    """)
    Long countWaitingTodayByPrefectureId(@Param("prefectureId") UUID prefectureId);

}

