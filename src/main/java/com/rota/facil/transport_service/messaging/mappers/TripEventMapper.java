package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.domain.enums.ActionType;
import com.rota.facil.transport_service.domain.enums.ResourceName;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.messaging.dto.send.trip.TripEventSend;
import com.rota.facil.transport_service.persistence.dto.StudentPersistenceDTO;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface TripEventMapper {
    @Mapping(target = "userEmail", source = "currentUser.email")
    @Mapping(target = "role", source = "currentUser.role")
    @Mapping(target = "actionTitle", expression = "java(currentUser.email() + actionType.getTitle() + \"viagem\")")
    @Mapping(target = "resourceName", expression = "java(ResourceName.TRIP)")
    @Mapping(target = "resourceId", source = "entity.id")
    @Mapping(target = "tripId", source = "entity.id")
    @Mapping(target = "prefectureId", source = "currentUser.prefectureId")
    TripEventSend map(TripEntity entity, CurrentUser currentUser, ActionType actionType);

    @Mapping(target = "tripId", source = "entity.id")
    TripEventSend map(TripEntity entity);

    @Mapping(target = "userEmail", source = "currentUser.email")
    @Mapping(target = "role", source = "currentUser.role")
    @Mapping(target = "resourceName", expression = "java(ResourceName.TRIP)")
    @Mapping(target = "resourceId", source = "entity.id")
    @Mapping(target = "tripId", source = "entity.id")
    @Mapping(target = "driverId", source = "currentUser.userId")
    @Mapping(target = "driverEmail", source = "currentUser.email")
    @Mapping(target = "prefectureId", source = "currentUser.prefectureId")
    @Mapping(target = "routeName", source = "entity.route.name")
    TripEventSend map(TripEntity entity, CurrentUser currentUser, ActionType actionType, String actionTitle, List<StudentPersistenceDTO> studentInfo);
}
