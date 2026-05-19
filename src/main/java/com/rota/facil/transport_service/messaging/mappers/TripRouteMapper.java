package com.rota.facil.transport_service.messaging.mappers;

import com.rota.facil.transport_service.http.dto.response.trip.TripRouteBoardPointResponseDTO;
import com.rota.facil.transport_service.http.dto.response.trip.TripRouteInstitutionResponseDTO;
import com.rota.facil.transport_service.http.dto.response.trip.TripRouteResponseDTO;
import com.rota.facil.transport_service.persistence.entities.BoardPointEntity;
import com.rota.facil.transport_service.persistence.entities.InstitutionEntity;
import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import com.rota.facil.transport_service.persistence.repositories.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TripRouteMapper {
    private final RouteRepository routeRepository;

    @Named(value = "mapRoute")
    public TripRouteResponseDTO mapRoute(TripEntity trip) {
        RouteEntity route = trip.getRoute();

        List<TripRouteInstitutionResponseDTO> institutionsToBeVisited = this.fetchInstitutionsToBeVisited(trip);
        List<TripRouteBoardPointResponseDTO> boardPointsToBeVisited = this.fetchBoardPointsToBeVisited(trip);

        return new TripRouteResponseDTO(route.getId(), route.getShift(), route.getGoing(), route.getReturn_(), route.getGoingFinish(), route.getReturnFinish(), institutionsToBeVisited, boardPointsToBeVisited);
    }

    @Named(value = "fetchInstitutionsToBeVisited")
    private List<TripRouteInstitutionResponseDTO> fetchInstitutionsToBeVisited(TripEntity tripEntity) {
        List<InstitutionEntity> institutionsToBeVisited = routeRepository.findAllInstitutionsById(tripEntity.getRoute().getId());
        institutionsToBeVisited.removeAll(tripEntity.getIgnoredInstitutions());
        return institutionsToBeVisited
                .stream()
                .map(i -> new TripRouteInstitutionResponseDTO(i.getId(), i.getName(), i.getLatitude(), i.getLongitude()))
                .toList();
    }

    @Named(value = "fetchBoardPointsToBeVisited")
    private List<TripRouteBoardPointResponseDTO> fetchBoardPointsToBeVisited(TripEntity tripEntity) {
        List<BoardPointEntity> boardPointsToBeVisited = routeRepository.findAllBoardPointsById(tripEntity.getRoute().getId());
        boardPointsToBeVisited.removeAll(tripEntity.getIgnoredBoardPoints());
        return boardPointsToBeVisited
                .stream()
                .map(b -> new TripRouteBoardPointResponseDTO(b.getId(), b.getName(), b.getLatitude(), b.getLongitude()))
                .toList();
    }
}
