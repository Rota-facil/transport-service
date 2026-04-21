package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.messaging.dto.receive.InstitutionEventReceive;
import com.rota.facil.transport_service.persistence.entities.InstitutionEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InstitutionEventMapper {
    @Mapping(target = "id", source = "institutionId")
    InstitutionEntity map(InstitutionEventReceive eventReceive);
}
