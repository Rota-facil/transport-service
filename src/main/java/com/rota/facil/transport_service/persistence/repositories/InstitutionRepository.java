package com.rota.facil.transport_service.persistence.repositories;

import com.rota.facil.transport_service.persistence.entities.InstitutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface InstitutionRepository extends JpaRepository<InstitutionEntity, UUID> {
    @Query("""
        SELECT i FROM InstitutionEntity i
        WHERE i.id IN (:ids)
    """)
    Set<InstitutionEntity> findAllSetById(@Param("ids") Iterable<UUID> ids);
}
