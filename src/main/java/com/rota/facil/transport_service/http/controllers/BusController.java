package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.BusService;
import com.rota.facil.transport_service.http.dto.request.bus.CreateBusRequestDTO;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.bus.BusResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/bus")
@RequiredArgsConstructor
public class BusController {
    private final BusService busService;

    @PostMapping
    public ResponseEntity<BusResponseDTO> createBus(
            @Valid @RequestBody CreateBusRequestDTO request,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(busService.register(request, currentUser));
    }

    @GetMapping("/{busId}")
    public ResponseEntity<BusResponseDTO> fetchBus(@PathVariable UUID busId) {
        return ResponseEntity.ok(busService.fetch(busId));
    }

    @GetMapping
    public ResponseEntity<List<BusResponseDTO>> listBus() {
        return ResponseEntity.ok(busService.list());
    }
}
