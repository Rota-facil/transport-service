package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "institutions_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionEntity {
    @Id
    @Column(name = "institution_id")
    private UUID id;

    private String name;

    private String latitude;

    private String longitude;
}
