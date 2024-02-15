package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="pointhash")
public class PointHash {

    @Id
    @Column(name= "point_hash_id")
    Long pointHashId;

    @Column(name = "point_coordinate")
    Point pointCoordinate;

    public static PointHash createPointHash(Long pointHashId, Point pointCoordinate){

        return PointHash.builder()
                .pointHashId(pointHashId)
                .pointCoordinate(pointCoordinate)
                .build();
    }
}
