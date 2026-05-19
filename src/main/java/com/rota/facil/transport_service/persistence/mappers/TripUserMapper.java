package com.rota.facil.transport_service.persistence.mappers;

import com.rota.facil.transport_service.http.dto.response.tripUser.SimpleTripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.response.tripUser.TripUserResponseDTO;
import com.rota.facil.transport_service.persistence.entities.TripUserEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface TripUserMapper {
    TripUserResponseDTO map(TripUserEntity entity);

    @Mapping(target = "institutionName", source = "institution.name")
    @Mapping(target = "boardPointName", source = "boardPoint.name")
    SimpleTripUserResponseDTO mapToSimple(TripUserEntity entity);
}
