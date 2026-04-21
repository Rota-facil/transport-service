package com.rota.facil.transport_service.persistence.entities;

import com.rota.facil.transport_service.domain.enums.Shift;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
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

    @Enumerated(EnumType.STRING)
    private Shift shift;

    private LocalTime going;

    @Column(name = "return")
    private LocalTime return_;

    private LocalTime goingFinish;

    private LocalTime returnFinish;

    @Builder.Default
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany
    @JoinTable(
            name = "routes_institutions_tb",
            joinColumns = @JoinColumn(name = "route_id"),
            inverseJoinColumns = @JoinColumn(name = "institution_id")
    )
    private Set<InstitutionEntity> institutions;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<BoardPointRouteEntity> boardPoints;
}
