package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.UserService;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.response.user.DriverResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/drivers")
    public ResponseEntity<List<DriverResponseDTO>> listDrivers(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(userService.listDrivers(currentUser));
    }
}
