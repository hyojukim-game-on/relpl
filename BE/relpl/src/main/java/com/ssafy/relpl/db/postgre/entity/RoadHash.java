package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="roadhash")
public class RoadHash {

    @Id
    @Column(name= "road_hash_id")
    Long roadHashId;

    @Column(name = "tmap_id")
    Long tmapId;

    public static RoadHash createRoadHash(Long roadHashId, Long tmapId){
        return RoadHash.builder()
                .roadHashId(roadHashId)
                .tmapId(tmapId)
                .build();
    }
}
