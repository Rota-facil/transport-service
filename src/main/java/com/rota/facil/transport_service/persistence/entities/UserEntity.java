package com.rota.facil.transport_service.persistence.entities;

import com.rota.facil.transport_service.domain.enums.DriverStatus;
import com.rota.facil.transport_service.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "users_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "user_id")
    private UUID id;

    @Column(name = "prefecture_id")
    private UUID prefectureId;

    private String name;

    private String email;

    @Builder.Default
    private Double score = 5.0;

    private String cpf;

    @Builder.Default
    private Boolean active = true;

    @OneToOne(mappedBy = "driver")
    private BusEntity bus;

    @Builder.Default
    @Column(name = "completed_trips")
    private Long completedTrips = 0L;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private DriverStatus status;

    public void update(UserEntity userEntity) {
        if (userEntity.getPrefectureId() != null) this.prefectureId = userEntity.getPrefectureId();
        if (userEntity.getName() != null) this.name = userEntity.getName();
        if (userEntity.getEmail() != null) this.email = userEntity.getEmail();
        if (userEntity.getScore() != null) this.score = userEntity.getScore();
        if (userEntity.getCpf() != null) this.cpf = userEntity.getCpf();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean isDriver() {
        return this.role.equals(Role.DRIVER);
    }

    public boolean isStudent() {
        return this.role.equals(Role.STUDENT);
    }

    public boolean isNotDriver() {
        return !this.isDriver();
    }

    public boolean isNotStudent() {
        return !this.isStudent();
    }

    public void moveToAvailable() {
        if (this.getRole().equals(Role.DRIVER)) this.setStatus(DriverStatus.AVAILABLE);
    }

    public void moveToOnRoute() {
        if (this.getRole().equals(Role.DRIVER)) this.setStatus(DriverStatus.ON_ROUTE);
    }

}
