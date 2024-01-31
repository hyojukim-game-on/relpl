package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.*;
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
@Table(name="roadinfo")
public class RoadInfo {

    @Id
    @Column(name= "road_info_id")
    @GeneratedValue(strategy=GenerationType.AUTO)
    Long roadId;

    @Column(name = "road_hash_id")
    Long roadHashId;

    @Column(name = "point_hash_id_start")
    Long pointHashIdStart;

    @Column(name = "point_hash_id_end")
    Long pointHashIdEnd;

    @Column(name = "road_info_len")
    int roadInfoLen;

    @Column(name = "road_info_report")
    int roadInfoReport;

    @Column(name = "road_info_total_report")
    int roadInfoTotalReport;

    @Column(name = "road_info_weight")
    int roadInfoWeight;
//
//    public static RoadInfo creatRoadInfo(RoadInfo road) {
//        return RoadInfo.builder()
//                .build();
//    }
//    public static PointHash createRoadInfo(Long pointHashId, Point pointCoordinate){
//
//        return PointHash.builder()
//                .pointHashId(pointHashId)
//                .pointCoordinate(pointCoordinate)
//                .build();
//    }
}
