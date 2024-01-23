package com.ssafy.relpl.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "roadhash")
public class RoadHash {
    private Long _id;
    private String tmap_id;

    public static RoadHash createRoadHash(Long id, String tmap_id){
        return RoadHash.builder()
                ._id(id)
                .tmap_id(tmap_id)
                .build();
    }
}
