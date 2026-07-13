package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.Progress;
import com.rota.facil.transport_service.domain.enums.Role;
import com.rota.facil.transport_service.domain.exceptions.BusNotAssociatedWithRouteException;
import com.rota.facil.transport_service.http.dto.request.trip.CreateTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.messaging.producers.RabbitTransportTripEventProducer;
import com.rota.facil.transport_service.persistence.entities.BusEntity;
import com.rota.facil.transport_service.persistence.entities.RouteEntity;
import com.rota.facil.transport_service.persistence.entities.TripEntity;
import com.rota.facil.transport_service.persistence.entities.UserEntity;
import com.rota.facil.transport_service.persistence.mappers.TripMapper;
import com.rota.facil.transport_service.persistence.repositories.BusRepository;
import com.rota.facil.transport_service.persistence.repositories.RouteRecurringRepository;
import com.rota.facil.transport_service.persistence.repositories.RouteRepository;
import com.rota.facil.transport_service.persistence.repositories.TripRepository;
import com.rota.facil.transport_service.persistence.repositories.TripUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripUserRepository tripUserRepository;

    @Mock
    private BusRepository busRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private RouteRecurringRepository routeRecurringRepository;

    @Mock
    private RabbitTransportTripEventProducer tripEventProducer;

    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private TripService tripService;

    @Test
    void shouldCreateManualTripForAssociatedBusInAuthenticatedPrefecture() {
        UUID prefectureId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();
        UUID busId = UUID.randomUUID();
        CurrentUser currentUser = new CurrentUser(UUID.randomUUID(), prefectureId, "admin@example.com", Role.ADMIN.name());
        RouteEntity route = RouteEntity.builder().id(routeId).name("Rota Centro").prefectureId(prefectureId).build();
        BusEntity bus = BusEntity.builder()
                .id(busId)
                .prefectureId(prefectureId)
                .driver(UserEntity.builder().id(UUID.randomUUID()).build())
                .build();

        when(busRepository.findByIdAndPrefectureId(busId, prefectureId)).thenReturn(Optional.of(bus));
        when(routeRepository.findByIdAndPrefectureId(routeId, prefectureId)).thenReturn(Optional.of(route));
        when(routeRecurringRepository.existsByRoute_IdAndBus_Id(routeId, busId)).thenReturn(true);
        when(tripRepository.save(any(TripEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        tripService.register(new CreateTripRequestDTO(routeId, busId), currentUser);

        ArgumentCaptor<TripEntity> tripCaptor = ArgumentCaptor.forClass(TripEntity.class);
        verify(tripRepository).save(tripCaptor.capture());
        TripEntity createdTrip = tripCaptor.getValue();
        assertEquals("Rota Centro", createdTrip.getName());
        assertEquals(prefectureId, createdTrip.getPrefectureId());
        assertEquals(route, createdTrip.getRoute());
        assertEquals(bus, createdTrip.getBus());
        assertEquals(Progress.NOT_STARTED, createdTrip.getActualStatus());
        assertEquals(1, createdTrip.getTripStatus().size());
        assertEquals(Progress.NOT_STARTED, createdTrip.getTripStatus().getFirst().getProgress());
        verify(tripEventProducer).createTripEvent(any(TripEntity.class));
        verify(tripMapper).map(any(TripEntity.class));
    }

    @Test
    void shouldRejectBusThatIsNotAssociatedWithRoute() {
        UUID prefectureId = UUID.randomUUID();
        UUID routeId = UUID.randomUUID();
        UUID busId = UUID.randomUUID();
        CurrentUser currentUser = new CurrentUser(UUID.randomUUID(), prefectureId, "admin@example.com", Role.ADMIN.name());
        RouteEntity route = RouteEntity.builder().id(routeId).name("Rota Centro").prefectureId(prefectureId).build();
        BusEntity bus = BusEntity.builder()
                .id(busId)
                .prefectureId(prefectureId)
                .driver(UserEntity.builder().id(UUID.randomUUID()).build())
                .build();

        when(busRepository.findByIdAndPrefectureId(busId, prefectureId)).thenReturn(Optional.of(bus));
        when(routeRepository.findByIdAndPrefectureId(routeId, prefectureId)).thenReturn(Optional.of(route));
        when(routeRecurringRepository.existsByRoute_IdAndBus_Id(routeId, busId)).thenReturn(false);

        assertThrows(
                BusNotAssociatedWithRouteException.class,
                () -> tripService.register(new CreateTripRequestDTO(routeId, busId), currentUser)
        );
    }

    @Test
    void shouldFilterDriverTripsByAuthenticatedPrefecture() {
        UUID userId = UUID.randomUUID();
        UUID prefectureId = UUID.randomUUID();
        CurrentUser currentUser = new CurrentUser(userId, prefectureId, "driver@example.com", Role.DRIVER.name());

        when(tripRepository.findAllTodayByDriverIdAndPrefectureId(userId, prefectureId))
                .thenReturn(List.of());

        assertTrue(tripService.myTripsToday(currentUser).isEmpty());
        verify(tripRepository).findAllTodayByDriverIdAndPrefectureId(userId, prefectureId);
    }

    @Test
    void shouldFilterStudentTripsByAuthenticatedPrefecture() {
        UUID userId = UUID.randomUUID();
        UUID prefectureId = UUID.randomUUID();
        CurrentUser currentUser = new CurrentUser(userId, prefectureId, "student@example.com", Role.STUDENT.name());

        when(tripUserRepository.findAllTodayByPassengerIdAndPrefectureId(userId, prefectureId))
                .thenReturn(List.of());

        assertTrue(tripService.myTripsToday(currentUser).isEmpty());
        verify(tripUserRepository).findAllTodayByPassengerIdAndPrefectureId(userId, prefectureId);
    }

    @Test
    void shouldListOnlyActiveTripsFromAuthenticatedPrefecture() {
        UUID prefectureId = UUID.randomUUID();
        CurrentUser currentUser = new CurrentUser(UUID.randomUUID(), prefectureId, "admin@example.com", Role.ADMIN.name());

        when(tripRepository.findAllActiveByPrefectureIdToday(prefectureId))
                .thenReturn(List.of());

        assertTrue(tripService.listActive(currentUser).isEmpty());
        verify(tripRepository).findAllActiveByPrefectureIdToday(prefectureId);
    }
}
