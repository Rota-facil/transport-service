package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.http.dto.response.user.DriverResponseDTO;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.id = :driverId
        AND u.role IN (com.rota.facil.transport_service.domain.enums.Role.DRIVER)
    """)
    Optional<UserEntity> findDriverById(@Param("driverId") UUID driverId);

    @Query("""
        SELECT COUNT(u) FROM UserEntity u
        WHERE u.prefectureId = :prefectureId
        AND u.role = com.rota.facil.transport_service.domain.enums.Role.STUDENT
    """)
    Long countStudentsByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT COUNT(u) FROM UserEntity u
        WHERE u.prefectureId = :prefectureId
        AND u.role = com.rota.facil.transport_service.domain.enums.Role.DRIVER
    """)
    Long countDriversByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.prefectureId = :prefectureId
        AND u.role = com.rota.facil.transport_service.domain.enums.Role.DRIVER
    """)
    List<UserEntity> findAllDriversByPrefectureId(@Param("prefectureId") UUID prefectureId);

    @Modifying
    @Query("""
        UPDATE UserEntity u
        SET u.completedTrips = u.completedTrips + 1
        WHERE u.id IN (:userIds)
    """)
    void increaseTripCompletedByUserIds(@Param("userIds") List<UUID> userIds);

    @Query("""
        SELECT u FROM UserEntity u
        WHERE u.id = :driverId
        AND u.prefectureId = :prefectureId
    """)
    Optional<UserEntity> findDriverByIdAndPrefectureId(@Param("driverId") UUID driverId, @Param("prefectureId") UUID prefectureId);
}
