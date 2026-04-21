package com.rota.facil.transport_service.persistence.mappers;

import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteBoardPointResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteResponseDTO;
import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.entities.BoardPointRouteEntity;
import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RouteMapper {
    RouteEntity map(CreateRouteRequestDTO request);

    @Mapping(target = "boardPoints", expression = "java(toBoardPointResponse(entity.getBoardPoints()))")
    RouteResponseDTO map(RouteEntity entity);

    default List<RouteBoardPointResponseDTO> toBoardPointResponse(List<BoardPointRouteEntity> entities) {
        if (entities == null || entities.isEmpty()) return List.of();
        return entities
                .stream()
                .map(
                        boardPointRoute ->
                        {
                            BoardPointEntity boardPoint = boardPointRoute.getBoardPoint();
                            return new RouteBoardPointResponseDTO(
                                    boardPointRoute.getId(),
                                    boardPoint.getName(),
                                    boardPoint.getLatitude(),
                                    boardPoint.getLongitude(),
                                    boardPointRoute.getBoardTimeGoing(),
                                    boardPointRoute.getBoardTimeFinish()
                                    );
                        }
                )
                .toList();
    }
}
