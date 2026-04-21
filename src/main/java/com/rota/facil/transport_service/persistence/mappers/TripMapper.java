package com.rota.facil.transport_service.persistence.mappers;

import com.rota.facil.transport_service.http.dto.request.trip.CreateTripRequestDTO;
import com.rota.facil.transport_service.http.dto.response.trip.TripResponseDTO;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface TripMapper {
    TripEntity map(CreateTripRequestDTO request);
    TripResponseDTO map(TripEntity entity);
}
