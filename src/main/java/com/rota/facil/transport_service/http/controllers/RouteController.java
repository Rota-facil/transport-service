package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.RouteService;
import com.rota.facil.transport_service.http.dto.request.route.CreateBoardPointRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @PostMapping("/register")
    public ResponseEntity<RouteResponseDTO> createRoute(@Valid @RequestBody CreateRouteRequestDTO request) {
        return ResponseEntity.ok(routeService.register(request));
    }

    @PostMapping("/{routeId}/board-points")
    public ResponseEntity<RouteResponseDTO> addBoardPointsToRoute(
            @PathVariable UUID routeId,
            @Valid @RequestBody List<CreateBoardPointRouteRequestDTO> request
    ) {
        return ResponseEntity.ok(routeService.addBoardPoints(routeId, request));
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteResponseDTO> fetchRoute(@PathVariable UUID routeId) {
        return ResponseEntity.ok(routeService.fetch(routeId));
    }

    @GetMapping
    public ResponseEntity<List<RouteResponseDTO>> listRoutes() {
        return ResponseEntity.ok(routeService.list());
    }

}
