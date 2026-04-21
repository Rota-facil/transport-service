package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
