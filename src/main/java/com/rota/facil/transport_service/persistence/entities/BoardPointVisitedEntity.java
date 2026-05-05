package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "board_points_visiteds_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardPointVisitedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "board_point_visited_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripEntity trip;

    @ManyToOne
    @JoinColumn(name = "board_point_id")
    private BoardPointEntity boardPoint;

    @Builder.Default
    private Boolean going = false;

    @Builder.Default
    @Column(name = "return")
    private Boolean return_ = false;
}
