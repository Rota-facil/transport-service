package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Entity
@Table(name = "board_points_tb")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardPointEntity {
    @Id
    @Column(name = "board_point_id")
    private UUID id;

    private String name;

    private String latitude;

    private String longitude;

    public void update(BoardPointEntity boardPointEntity) {
        if (boardPointEntity.getName() != null) this.name = boardPointEntity.getName();
        if (boardPointEntity.getLatitude() != null) this.latitude = boardPointEntity.getLatitude();
        if (boardPointEntity.getLongitude() != null) this.longitude = boardPointEntity.getLongitude();
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
