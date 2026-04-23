package com.rota.facil.transport_service.jobs.schedule;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import com.rota.facil.transport_service.persistence.entities.RouteRecurringEntity;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import com.rota.facil.transport_service.persistence.entities.TripStatusEntity;
import com.rota.facil.transport_service.persistence.repositories.RouteRecurringRepository;
import com.rota.facil.transport_service.persistence.repositories.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TripSchedule {
    private final TripRepository tripRepository;
    private final RouteRecurringRepository routeRecurringRepository;

    @Scheduled(cron = "0 0 3 * * *", zone = "America/Sao_Paulo")
    public void createTrip() {
        List<RouteRecurringEntity> routeRecurringEntities = routeRecurringRepository.findAllRouteRecurringToday(DaysOfWeek.getFromValueDay(LocalDate.now().getDayOfWeek().getValue()));

        List<TripEntity> tripEntities = new ArrayList<>();

        for (RouteRecurringEntity routeRecurring : routeRecurringEntities) {
            TripEntity preSavedTrip = TripEntity.builder()
                    .route(routeRecurring.getRoute())
                    .bus(routeRecurring.getBusEntity())
                    .tripStatus(new ArrayList<>())
                    .build();
            preSavedTrip.getTripStatus().add(TripStatusEntity.builder().trip(preSavedTrip).build());

            tripEntities.add(preSavedTrip);
        }

        tripRepository.saveAll(tripEntities);
    }
}
