package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.metric.MetricResponse;
import com.rota.facil.transport_service.persistence.repositories.BusRepository;
import com.rota.facil.transport_service.persistence.repositories.RouteRepository;
import com.rota.facil.transport_service.persistence.repositories.TripRepository;
import com.rota.facil.transport_service.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricService {
    private final BusRepository busRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;

    public MetricResponse fetch(CurrentUser currentUser) {
        Long activeRoutes = routeRepository.countByPrefectureId(currentUser.prefectureId());
        Long tripsToday = tripRepository.countTodayByPrefectureId(currentUser.prefectureId());
        Long cancelledTrips = tripRepository.countCancelledByPrefectureId(currentUser.prefectureId());

        Long students = userRepository.countStudentsByPrefectureId(currentUser.prefectureId());
        Long drivers = userRepository.countDriversByPrefectureId(currentUser.prefectureId());

        Long bus = busRepository.countByPrefectureId(currentUser.prefectureId());

        return new MetricResponse(
                activeRoutes,
                tripsToday,
                cancelledTrips,
                students,
                drivers,
                bus
        );
    }
}
