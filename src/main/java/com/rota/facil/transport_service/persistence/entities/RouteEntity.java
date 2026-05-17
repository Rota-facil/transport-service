package com.rota.facil.transport_service.persistence.entities;

import com.rota.facil.transport_service.domain.enums.DaysOfWeek;
import com.rota.facil.transport_service.domain.enums.Delay;
import com.rota.facil.transport_service.domain.enums.Progress;
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
@Getter @Setter
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

    private String interpretation;

    @Column(name = "prefecture_id")
    private UUID prefectureId;

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

    @ElementCollection
    @CollectionTable(name = "route_recurring_day_of_week_tb", joinColumns = @JoinColumn(name = "route_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "days_of_week")
    private Set<DaysOfWeek> daysOfWeek;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<BoardPointRouteEntity> boardPoints;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private List<RouteRecurringEntity> recurring;

    @OneToMany(mappedBy = "route")
    private List<TripEntity> trips;

    public Delay calculateDelay(LocalTime arrivalDate, Progress progress) {
        return this.buildDelay(arrivalDate, this.returnFinish.plusMinutes(5L));
    }

    private Delay buildDelay(LocalTime arrivalDate, LocalTime timeToCompare) {
        if (arrivalDate.equals(timeToCompare)) return Delay.PUNCTUAL;
        else if (arrivalDate.isBefore(timeToCompare)) return Delay.EARLY;
        else return Delay.LATE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RouteEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
