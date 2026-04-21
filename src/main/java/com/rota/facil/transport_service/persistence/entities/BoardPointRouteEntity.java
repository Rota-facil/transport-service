package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "board_points_routes_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardPointRouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "board_point_route_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "board_point_id")
    private BoardPointEntity boardPoint;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @Column(name = "board_time_going")
    private LocalTime boardTimeGoing;

    @Column(name = "board_time_finish")
    private LocalTime boardTimeFinish;
}
