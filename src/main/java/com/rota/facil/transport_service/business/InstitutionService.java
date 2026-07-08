package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.exceptions.InstitutionNotFoundException;
import com.rota.facil.transport_service.persistence.entities.InstitutionEntity;
import com.rota.facil.transport_service.persistence.repositories.InstitutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstitutionService {
    private final InstitutionRepository institutionRepository;

    public void register(InstitutionEntity institutionEntity) {
        institutionEntity.setDeleted(false);
        institutionEntity.setGeom();
        institutionRepository.save(institutionEntity);
    }

    public void update(InstitutionEntity institutionEntity) {
        InstitutionEntity institutionFound = this.fetchEntity(institutionEntity.getId());
        if (institutionFound.getDeleted()) return;
        institutionFound.update(institutionEntity);
        institutionRepository.save(institutionFound);
    }

    public void delete(InstitutionEntity institutionEntity) {
        InstitutionEntity institutionFound = this.fetchEntity(institutionEntity.getId());
        institutionFound.markAsDeleted();
        institutionRepository.save(institutionFound);
    }

    private InstitutionEntity fetchEntity(UUID institutionId) {
        return institutionRepository.findById(institutionId)
                .orElseThrow(InstitutionNotFoundException::new);
    }
}
