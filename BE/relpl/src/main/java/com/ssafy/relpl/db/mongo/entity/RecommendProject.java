package com.ssafy.relpl.db.mongo.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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

    public static RecommendProject createRecommendProject(List<Point> lineString) {
        return RecommendProject.builder()
                .recommendLineString(new GeoJsonLineString(lineString))
                .build();
    }
}
