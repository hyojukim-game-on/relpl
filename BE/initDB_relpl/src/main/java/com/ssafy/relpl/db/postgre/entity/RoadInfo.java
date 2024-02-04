package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="roadinfo")
public class RoadInfo {

    @Id
    @Column(name= "road_info_id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
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

    @Column(name = "road_Info_weight")
    int roadInfoRecentDone;

    public static RoadInfo createRoadInfo(Long roadHashId, Long pointHashIdStart, Long pointHashIdEnd, int roadInfoLen) {
        return RoadInfo.builder()
                .roadHashId(roadHashId)
                .pointHashIdStart(pointHashIdStart)
                .pointHashIdEnd(pointHashIdEnd)
                .roadInfoLen(roadInfoLen)
                .build();
    }
}
