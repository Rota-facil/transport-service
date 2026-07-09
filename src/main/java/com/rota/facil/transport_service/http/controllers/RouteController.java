package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.RouteService;
import com.rota.facil.transport_service.http.dto.request.route.CreateBoardPointRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.CreateRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.route.UpdateRouteRequestDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.client.intelligence.RouteInterpretationResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteHeatMapResponseDTO;
import com.rota.facil.transport_service.http.dto.response.route.RouteResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
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

    @PostMapping("/{routeId}/interpreter")
    public ResponseEntity<RouteInterpretationResponseDTO> interpreterRoute(
            @PathVariable UUID routeId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(routeService.interpreterRoute(routeId, currentUser));
    }

    @GetMapping("/{routeId}/interpretations")
    public ResponseEntity<List<RouteInterpretationResponseDTO>> listInterpretations(
            @PathVariable UUID routeId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(routeService.listInterpretations(routeId, currentUser));
    }

    @DeleteMapping("/{routeId}/interpretations/{interpretationId}")
    public ResponseEntity<Void> deleteInterpretation(
            @PathVariable UUID routeId,
            @PathVariable UUID interpretationId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        routeService.deleteInterpretation(routeId, interpretationId, currentUser);
        return ResponseEntity.noContent().build();
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

    @PutMapping("/{routeId}")
    public ResponseEntity<RouteResponseDTO> updateRoute(
            @PathVariable UUID routeId,
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody UpdateRouteRequestDTO request
    ) {
       return ResponseEntity.ok(routeService.update(routeId, currentUser, request));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable UUID routeId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        routeService.delete(routeId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/simple")
    public ResponseEntity<List<RouteResponseDTO>> listSimpleRoutes(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(routeService.listSimple(currentUser));
    }

    @GetMapping
    public ResponseEntity<Page<RouteResponseDTO>> listRoutes(
            @ParameterObject @PageableDefault Pageable pageable,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(routeService.list(currentUser, pageable));
    }

}
