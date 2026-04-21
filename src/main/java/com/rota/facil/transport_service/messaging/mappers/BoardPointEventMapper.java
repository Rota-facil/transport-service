package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.messaging.dto.receive.BoardPointEventReceive;
import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardPointEventMapper {
    @Mapping(target = "id", source = "boardPointId")
    BoardPointEntity map(BoardPointEventReceive eventReceive);
}
