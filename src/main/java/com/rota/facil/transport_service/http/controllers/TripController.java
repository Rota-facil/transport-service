package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.TripService;
import com.rota.facil.transport_service.http.dto.request.trip.CancelTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.JoinUserInTrip;
import com.rota.facil.transport_service.http.dto.response.tripUser.SimpleTripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.response.tripUser.TripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.trip.TripResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
@RequiredArgsConstructor
public class TripController {
    private final TripService tripService;

    @PostMapping("/process")
    public ResponseEntity<Void> processTrip(
            @RequestParam UUID tripId,
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        tripService.processTrip(tripId, latitude, longitude);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripId}/join")
    public ResponseEntity<TripUserResponseDTO> joinInTrip(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal CurrentUser user,
            @Valid @RequestBody JoinUserInTrip request
    ) {
        return ResponseEntity.ok(tripService.join(tripId, user, request));
    }

    @PostMapping("/{tripId}/init")
    public ResponseEntity<TripResponseDTO> initTrip(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(tripService.init(tripId, currentUser));
    }

    @PostMapping("/{tripId}/cancel")
    public ResponseEntity<TripResponseDTO> cancelTrip(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @RequestBody CancelTripRequestDTO request
    ) {
        return ResponseEntity.ok(tripService.cancel(tripId, currentUser, request));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponseDTO> fetchTrip(
            @PathVariable UUID tripId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(tripService.fetch(tripId, currentUser));
    }

    @GetMapping("/{tripId}/students")
    public ResponseEntity<List<SimpleTripUserResponseDTO>> listStudentsOfTrip(@PathVariable UUID tripId, @AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(tripService.listStudents(tripId, currentUser));
    }

    @GetMapping("/my-trips")
    public ResponseEntity<List<TripResponseDTO>> fetchMyTripsToday(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(tripService.myTripsToday(currentUser));
    }


    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> listTrip(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(tripService.list(currentUser));
    }

}
