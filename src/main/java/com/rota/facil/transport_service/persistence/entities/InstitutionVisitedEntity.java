package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "institutions_visiteds_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionVisitedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "institution_visited_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    private InstitutionEntity institution;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private TripEntity trip;

    @Builder.Default
    private Boolean going = false;

    @Builder.Default
    @Column(name = "return")
    private Boolean return_ = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InstitutionEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
