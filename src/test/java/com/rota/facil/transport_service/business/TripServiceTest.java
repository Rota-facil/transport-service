package com.rota.facil.transport_service.business;

import com.rota.facil.transport_service.domain.enums.Role;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.persistence.mappers.TripMapper;
import com.rota.facil.transport_service.persistence.repositories.TripRepository;
import com.rota.facil.transport_service.persistence.repositories.TripUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @Mock
    private TripRepository tripRepository;

    @Mock
    private TripUserRepository tripUserRepository;

    @Mock
    private TripMapper tripMapper;

    @InjectMocks
    private TripService tripService;

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
