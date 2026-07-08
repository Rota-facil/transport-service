package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "route_interpretations_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RouteInterpretationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "route_interpretation_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private RouteEntity route;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String interpretation;

    @Builder.Default
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
