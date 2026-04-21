package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "routes_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "route_id")
    private UUID id;

    private String shift;

    private LocalTime going;

    @Column(name = "return")
    private LocalTime return_;

    private LocalTime goingFinish;

    private LocalTime returnFinish;

    @ManyToMany
    @JoinTable(
            name = "routes_institutions_tb",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "institution_id")
    )
    private Set<InstitutionEntity> institution;
}
