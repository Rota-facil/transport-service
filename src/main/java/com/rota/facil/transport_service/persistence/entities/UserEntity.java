package com.rota.facil.transport_service.persistence.entities;

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

    @Enumerated(EnumType.STRING)
    private Role role;
}
