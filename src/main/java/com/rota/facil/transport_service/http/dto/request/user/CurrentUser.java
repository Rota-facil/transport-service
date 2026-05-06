package com.rota.facil.transport_service.http.dto.request.user;

import com.rota.facil.transport_service.domain.enums.Role;

import java.util.UUID;

public record CurrentUser(
        UUID userId,
        UUID prefectureId,
        String email,
        String role
) {

    public boolean isStudent() {
        return role.equals(Role.STUDENT.name());
    }

    public boolean isDriver() {
        return role.equals(Role.DRIVER.name());
    }

    public boolean isNotStudent() {
        return !this.isStudent();
    }

    public boolean isNotDriver() {
        return !this.isDriver();
    }
}