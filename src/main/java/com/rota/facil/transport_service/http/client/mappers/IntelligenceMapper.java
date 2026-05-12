package com.rota.facil.transport_service.http.client.mappers;

import com.rota.facil.transport_service.http.dto.request.client.intelligence.RouteInformationRequestDTO;
import com.rota.facil.transport_service.http.dto.request.client.intelligence.RouteInformationTripHistory;
import com.rota.facil.transport_service.http.dto.request.client.intelligence.RouteInformationTripStatus;
import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import com.rota.facil.transport_service.persistence.repositories.InstitutionRepository;
import com.rota.facil.transport_service.persistence.repositories.InstitutionVisitedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class IntelligenceMapper {
    private final InstitutionVisitedRepository institutionVisitedRepository;
    private final InstitutionRepository institutionRepository;

    public RouteInformationRequestDTO map(RouteEntity route, List<TripEntity> trips) {
        List<RouteInformationTripHistory> histories = trips.stream()
                .map(trip -> {
                    List<String> expectedInstitutionsPassed = institutionRepository.findAllInstitutionsNameToBeVisitedByRouteId(route.getId());
                    List<String> actualInstitutionsVisiteds = institutionVisitedRepository.findAllInstitutionsNameVisitedsByTripId(trip.getId());
                    List<RouteInformationTripStatus> statuses = trip.getTripStatus().stream().map(status -> new RouteInformationTripStatus(status.getProgress(), status.getDelay(), status.getCreatedAt())).toList();
                    return new RouteInformationTripHistory(expectedInstitutionsPassed, actualInstitutionsVisiteds, statuses);
                }).toList();

        return new RouteInformationRequestDTO(route.getGoing(), route.getGoingFinish(), route.getReturn_(), route.getReturnFinish(), route.getShift(), route.getDaysOfWeek(), histories);
    }
}
