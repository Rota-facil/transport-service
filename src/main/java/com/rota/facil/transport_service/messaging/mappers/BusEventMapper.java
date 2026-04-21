package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.messaging.dto.send.BusEventSend;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BusEventMapper {
    @Mapping(target = "busId", source = "id")
    BusEventSend map(BusEntity entity);
}
