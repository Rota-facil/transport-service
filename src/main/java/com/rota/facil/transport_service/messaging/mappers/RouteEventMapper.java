package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.domain.enums.ActionType;
import com.rota.facil.transport_service.http.dto.request.CurrentUser;
import com.rota.facil.transport_service.messaging.dto.send.RouteEventSend;
import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RouteEventMapper {
    @Mapping(target = "actionTitle", expression = "java(currentUser.email() + actionType.getTitle() + \"rota\")")
    @Mapping(target = "resourceName", expression = "java(ResourceName.ROUTE)")
    @Mapping(target = "resourceId", source = "entity.id")
    RouteEventSend map(RouteEntity entity, CurrentUser currentUser, ActionType actionType);
}
