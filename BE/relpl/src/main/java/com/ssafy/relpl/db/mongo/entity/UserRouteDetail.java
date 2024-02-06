package com.ssafy.relpl.db.mongo.entity;

//import com.ssafy.relpl.dto.request.ProjectStopRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Builder
@Document(collection = "userroutedetail")
public class UserRouteDetail {

    @Id
    private String id;
    private Long userMoveId;
    private Long userId;
    private Long projectId;
    private GeoJsonLineString recommendLineString;

    // 상세릴레이 정보 조회 로직 실행을 위해서 일단 재성님이 주석처리 오더 내리심.
//    public static UserRouteDetail createUserRouteDetail(ProjectStopRequest request) {
//        return UserRouteDetail.builder()
//                .userId(request.getUserId())
//                .projectId(request.getProjectId())
//                .recommendLineString(convertToGeoJsonLineString(request.getUserMovePath()))
//                .build();
//    }

    public static GeoJsonLineString convertToGeoJsonLineString(List<Point> points) {
        List<org.springframework.data.geo.Point> coordinates = points.stream()
                .map(point -> new org.springframework.data.geo.Point(point.getCoordinate().getX(), point.getCoordinate().getY()))
                .collect(Collectors.toList());

        return new GeoJsonLineString(coordinates);
    }


}
