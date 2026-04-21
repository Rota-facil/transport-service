package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "board_points_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardPointEntity {
    @Id
    @Column(name = "board_point_id")
    private UUID id;

    private String name;

    private String latitude;

    private String longitude;
}
