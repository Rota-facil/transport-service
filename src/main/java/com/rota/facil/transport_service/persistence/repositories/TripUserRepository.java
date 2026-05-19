package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.entities.InstitutionEntity;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import com.rota.facil.transport_service.persistence.entities.TripUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    @Query("""
        SELECT COUNT(tu) FROM TripUserEntity tu
        INNER JOIN tu.trip t
        WHERE t.id = :tripId
        AND tu.going = :going
        AND tu.return_ = :return
    """)
    int countPassengersByTripIdAndGoingAndReturn(@Param("tripId") UUID tripId, @Param("going") boolean going, @Param("return") boolean return_);

    @Query("""
        SELECT u.email FROM TripUserEntity tu
        INNER JOIN tu.user u
        INNER JOIN tu.trip t
        WHERE t.id = :tripId
    """)
    List<String> findAllEmailsByTripId(@Param("tripId") UUID tripId);

    @Query("""
        SELECT t FROM TripUserEntity tu
        INNER JOIN tu.trip t
        INNER JOIN t.bus b
        INNER JOIN b.driver d
        WHERE d.id = :driverId
        AND CAST(t.createdAt AS DATE) = CURRENT_DATE
    """)
    List<TripEntity> findAllByDriverId(@Param("driverId") UUID driverId);

    @Query("""
        SELECT t FROM TripUserEntity tu
        INNER JOIN tu.user u
        INNER JOIN tu.trip t
        WHERE u.id = :passengerId
        AND t.createdAt = CURRENT_DATE
    """)
    List<TripEntity> findAllTodayByPassengerId(@Param("passengerId") UUID passengerId);

    @Query("""
        SELECT i FROM TripUserEntity tu
        INNER JOIN tu.institution i
        INNER JOIN tu.trip t
        WHERE t.id = :tripId
    """)
    List<InstitutionEntity> findAllInstitutionsByTripId(@Param("tripId") UUID tripId);

    @Query("""
        SELECT i FROM TripUserEntity tu
        INNER JOIN tu.institution i
        INNER JOIN tu.trip t
        WHERE t.id = :tripId
        AND tu.going IS TRUE
    """)
    List<InstitutionEntity> findAllInstitutionsGoingByTripId(@Param("tripId") UUID tripId);

    @Query("""
        SELECT i FROM TripUserEntity tu
        INNER JOIN tu.institution i
        INNER JOIN tu.trip t
        WHERE t.id = :tripId
        AND tu.return_ IS TRUE
    """)
    List<InstitutionEntity> findAllInstitutionsReturnByTripId(@Param("tripId") UUID tripId);

    @Query("""
        SELECT b FROM TripUserEntity tu
        INNER JOIN tu.boardPoint b
        INNER JOIN tu.trip t
        WHERE t.id = :tripId
        AND tu.going IS TRUE
    """)
    List<BoardPointEntity> findAllBoardPointsGoingByTripId(@Param("tripId") UUID tripId);

    @Query("""
        SELECT b FROM TripUserEntity tu
        INNER JOIN tu.boardPoint b
        INNER JOIN tu.trip t
        WHERE t.id = :tripId
        AND tu.return_ IS TRUE
    """)
    List<BoardPointEntity> findAllBoardPointsReturnByTripId(@Param("tripId") UUID tripId);

    @Query(value = """
    select b.* from trip_users_tb tu
    INNER JOIN board_points_tb b USING(board_point_id)
    INNER JOIN trips_tb t USING(trip_id)
    INNER JOIN trip_status_tb ts USING(trip_id)
    INNER JOIN routes_tb r USING(route_id)
    WHERE r.route_id = :routeId
    AND ts.progress = 'RETURN_FINISHED'
    ORDER BY geom;
    """, nativeQuery = true)
    List<BoardPointEntity> findAllBoardPointsOfTripsFinishedByRouteId(@Param("routeId") UUID routeId);

    @Query("""
        SELECT tu FROM TripUserEntity tu
        INNER JOIN tu.trip t
        INNER JOIN t.bus b
        INNER JOIN b.driver d
        WHERE t.id = :tripId
        AND d.id = :driverId
    """)
    List<TripUserEntity> findAllByDriverIdAndTripId(@Param("driverId") UUID driverId, @Param("tripId") UUID tripId);
}
