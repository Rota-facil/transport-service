package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.messaging.dto.receive.UserEventReceive;
import com.rota.facil.transport_service.messaging.dto.send.FeedbackUserEventSend;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserEventMapper {
    @Mapping(target = "id", source = "userId")
    UserEntity map(UserEventReceive userEvent);

    FeedbackUserEventSend map(UUID userId, double note);
}
