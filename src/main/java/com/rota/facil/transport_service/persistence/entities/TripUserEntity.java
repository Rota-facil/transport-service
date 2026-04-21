package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.util.UUID;

@Builder
@Entity
@Table(name = "trip_users_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trip_user_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private InstitutionEntity institution;

    @ManyToOne
    @JoinColumn(name = "board_point_id")
    private BoardPointEntity boardPoint;

    @Builder.Default
    private Boolean present = false;

    private Boolean going;

    @Column(name = "return")
    private Boolean return_;
}
