package com.rota.facil.transport_service.persistence.entities;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "route_recurring_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteRecurringEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "route_recurring_id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private BusEntity busEntity;

    @ElementCollection
    @CollectionTable(name = "route_recurring_day_of_week_tb", joinColumns = @JoinColumn(name = "route_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "days_of_week")
    private Set<DaysOfWeek> daysOfWeek;
}
