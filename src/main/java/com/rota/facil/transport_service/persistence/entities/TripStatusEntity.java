package com.rota.facil.transport_service.persistence.entities;

import com.rota.facil.transport_service.domain.enums.Delay;
import com.rota.facil.transport_service.domain.enums.Progress;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "trip_status_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TripStatusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "trip_status_id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "trip_id")
    private TripEntity trip;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Progress progress = Progress.NOT_STARTED;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Delay delay = Delay.PUNCTUAL;

    @Builder.Default
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
