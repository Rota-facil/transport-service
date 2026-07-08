package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.messaging.dto.receive.BoardPointEventReceive;
import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoardPointEventMapper {
    @Mapping(target = "id", source = "boardId")
    @Mapping(target = "deleted", ignore = true)
    BoardPointEntity map(BoardPointEventReceive eventReceive);
}
