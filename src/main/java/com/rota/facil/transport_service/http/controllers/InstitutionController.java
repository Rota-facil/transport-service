package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.InstitutionService;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.institution.InstitutionRouteCountResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/institutions")
@RequiredArgsConstructor
public class InstitutionController {
    private final InstitutionService institutionService;

    @GetMapping("/route-counts")
    public ResponseEntity<List<InstitutionRouteCountResponseDTO>> listRouteCounts(
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return ResponseEntity.ok(institutionService.listRouteCounts(currentUser));
    }
}
