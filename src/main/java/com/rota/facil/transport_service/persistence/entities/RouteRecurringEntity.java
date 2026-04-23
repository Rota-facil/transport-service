package com.rota.facil.transport_service.persistence.entities;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Entity
@Table(name = "route_recurring_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteRecurringEntity {
    @Id
    @OneToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private BusEntity busEntity;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "finish_date")
    private LocalDate finishDate;

    @ElementCollection
    @CollectionTable(name = "route_recurring_day_of_week_tb", joinColumns = @JoinColumn(name = "route_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "days_of_week")
    private Set<DaysOfWeek> daysOfWeek;
}
