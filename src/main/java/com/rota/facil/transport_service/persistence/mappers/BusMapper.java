package com.rota.facil.transport_service.persistence.mappers;

import com.rota.facil.transport_service.http.dto.request.bus.CreateBusRequestDTO;
import com.rota.facil.transport_service.http.dto.response.bus.BusResponseDTO;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface BusMapper {
    BusEntity map(CreateBusRequestDTO request);
    BusResponseDTO map(BusEntity entity);
}
