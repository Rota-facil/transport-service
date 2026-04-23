package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Table(name = "trips_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trip_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private BusEntity bus;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @Column(name = "reason_of_cancellation")
    private String reasonOfCancellation;

    @Builder.Default
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDate createdAt = LocalDate.now();

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
    private List<TripStatusEntity> tripStatus;
}
