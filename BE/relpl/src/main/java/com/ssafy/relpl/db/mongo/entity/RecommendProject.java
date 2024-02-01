package com.ssafy.relpl.db.mongo.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiLineString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@Document(collection = "recommendproject")
public class RecommendProject {

    @Id
    private String id;
    private String projectId;
    private GeoJsonLineString recommendLineString;
//    private GeoJsonMultiLineString
//    private int lanetype;
//    private int speed;
//    private int total_distance;
//    private int lane;
}
