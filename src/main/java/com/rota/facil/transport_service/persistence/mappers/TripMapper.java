package com.rota.facil.transport_service.persistence.mappers;

import com.rota.facil.transport_service.http.dto.response.trip.TripResponseDTO;
import com.rota.facil.transport_service.messaging.mappers.TripRouteMapper;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true), uses = TripRouteMapper.class)
public interface TripMapper {
    @Mapping(target = "route", source = ".", qualifiedByName = "mapRoute")
    TripResponseDTO map(TripEntity entity);
}
