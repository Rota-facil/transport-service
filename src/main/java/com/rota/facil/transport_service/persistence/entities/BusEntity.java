package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "bus_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bus_id")
    private UUID id;

    private UUID prefectureId;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private UserEntity driver;

    private Long capacity;

    private String plate;

    @Builder.Default
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
