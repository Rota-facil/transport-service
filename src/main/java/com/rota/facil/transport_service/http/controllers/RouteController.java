package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.RouteService;
import com.rota.facil.transport_service.http.dto.request.route.CreateBoardPointRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.client.intelligence.RouteInterpretationResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteHeatMapResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {
    private final RouteService routeService;

    @PostMapping("/register")
    public ResponseEntity<RouteResponseDTO> createRoute(
            @Valid @RequestBody CreateRouteRequestDTO request,
            @AuthenticationPrincipal CurrentUser currentUser
            ) {
        return ResponseEntity.ok(routeService.register(request, currentUser));
    }

//    @PostMapping("/{routeId}/board-points")
//    public ResponseEntity<RouteResponseDTO> addBoardPointsToRoute(
//            @PathVariable UUID routeId,
//            @Valid @RequestBody List<CreateBoardPointRouteRequestDTO> request,
//            @AuthenticationPrincipal CurrentUser currentUser
//    ) {
//        return ResponseEntity.ok(routeService.addBoardPoints(routeId, request, currentUser));
//    }

    @PostMapping("/{routeId}/interpreter")
    public ResponseEntity<RouteInterpretationResponseDTO> interpreterRoute(@PathVariable UUID routeId) {
        return ResponseEntity.ok(routeService.interpreterRoute(routeId));
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteResponseDTO> fetchRoute(
            @PathVariable UUID routeId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(routeService.fetch(routeId, currentUser));
    }

    @PostMapping("/{routeId}/board-point/heat-map")
    public ResponseEntity<RouteHeatMapResponseDTO> generateRouteBoardPointHeatMap(
            @PathVariable UUID routeId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(routeService.generateRouteBoardPointHeatMap(routeId, currentUser));
    }

    @GetMapping
    public ResponseEntity<List<RouteResponseDTO>> listRoutes(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(routeService.list(currentUser));
    }

}
