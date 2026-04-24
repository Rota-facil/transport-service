package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
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


    @OneToMany
    List<RouteEntity> routes;

    public void update(InstitutionEntity institutionEntity) {
        if (institutionEntity.getName() != null) this.name = institutionEntity.getName();
        if (institutionEntity.getLatitude() != null) this.latitude = institutionEntity.getLatitude();
        if (institutionEntity.getLongitude() != null) this.longitude = institutionEntity.getLongitude();
    }

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
