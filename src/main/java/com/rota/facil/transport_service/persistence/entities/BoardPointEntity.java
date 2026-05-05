package com.rota.facil.transport_service.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

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

    private Double latitude;

    private Double longitude;

    @Column(columnDefinition = "geography(POINT, 4326)")
    private Point geom;

    public void update(BoardPointEntity boardPointEntity) {
        if (boardPointEntity.getName() != null) this.name = boardPointEntity.getName();
        if (boardPointEntity.getLatitude() != null) this.latitude = boardPointEntity.getLatitude();
        if (boardPointEntity.getLongitude() != null) this.longitude = boardPointEntity.getLongitude();
        if (boardPointEntity.getLatitude() != null && boardPointEntity.getLongitude() != null) this.setGeom();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardPointEntity that)) return false;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public void setGeom() {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        this.geom = geometryFactory.createPoint(new Coordinate(this.longitude, this.latitude));
    }
}
