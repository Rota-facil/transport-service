package com.rota.facil.transport_service.persistence.mappers;

import com.rota.facil.transport_service.http.dto.response.user.DriverResponseDTO;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    DriverResponseDTO mapToDriver(UserEntity entity);
}
