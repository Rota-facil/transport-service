package com.rota.facil.transport_service.persistence.mappers;

import com.rota.facil.transport_service.http.dto.request.user.EvaluateUserRequestDTO;
import com.rota.facil.transport_service.http.dto.response.user.EvaluateUserResponseDTO;
import com.rota.facil.transport_service.persistence.entities.FeedBackEntity;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface FeedBackMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "feedback", source = "request.feedback")
    @Mapping(target = "note", source = "request.note")
    FeedBackEntity map(EvaluateUserRequestDTO request, UserEntity sender, UserEntity receiver);

    @Mapping(target = "senderEmail", source = "sender.email")
    @Mapping(target = "receiverEmail", source = "receiver.email")
    EvaluateUserResponseDTO map(FeedBackEntity entity);
}
