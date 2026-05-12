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

    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @ManyToOne
    @JoinColumn(name = "bus_id")
    private BusEntity bus;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteRecurringEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
