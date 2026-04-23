package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.TripService;
import com.rota.facil.transport_service.http.dto.request.trip.CancelTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.CreateTripRequestDTO;
import com.rota.facil.transport_service.http.dto.request.trip.JoinUserInTrip;
import com.rota.facil.transport_service.http.dto.request.tripUser.TripUserResponseDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.trip.TripResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
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

//    @PostMapping("/register")
//    public ResponseEntity<TripResponseDTO> createTrip(@Valid @RequestBody CreateTripRequestDTO request) {
//        return ResponseEntity.ok(tripService.register(request));
//    }

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
    public ResponseEntity<TripResponseDTO> fetchTrip(@PathVariable UUID tripId) {
        return ResponseEntity.ok(tripService.fetch(tripId));
    }

    @GetMapping("/my-trips")
    public ResponseEntity<List<TripUserResponseDTO>> fetchMyTrips(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(tripService.myTrips(currentUser));
    }


    @GetMapping
    public ResponseEntity<List<TripResponseDTO>> listTrip() {
        return ResponseEntity.ok(tripService.list());
    }

}
