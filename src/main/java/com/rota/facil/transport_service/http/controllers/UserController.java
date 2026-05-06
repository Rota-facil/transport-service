package com.rota.facil.transport_service.http.controllers;

import com.rota.facil.transport_service.business.FeedBackService;
import com.rota.facil.transport_service.business.UserService;
import com.rota.facil.transport_service.http.dto.request.user.CurrentUser;
import com.rota.facil.transport_service.http.dto.request.user.EvaluateUserRequestDTO;
import com.rota.facil.transport_service.http.dto.response.user.EvaluateUserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedBackService feedBackService;

    @PostMapping("/{userId}/evaluate")
    public ResponseEntity<EvaluateUserResponseDTO> evaluateUser(
            @AuthenticationPrincipal CurrentUser currentUser,
            @Valid @RequestBody EvaluateUserRequestDTO request,
            @PathVariable UUID userId
    )   {
        return ResponseEntity.ok(feedBackService.evaluate(currentUser, userId, request));
    }
}
