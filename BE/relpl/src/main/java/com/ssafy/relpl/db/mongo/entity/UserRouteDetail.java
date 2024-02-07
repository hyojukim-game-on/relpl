package com.ssafy.relpl.db.mongo.entity;
import com.ssafy.relpl.dto.request.ProjectStopRouteRequest;
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
    private String userRouteDetailId;
    private Long userRouteId;
    private Long userId;
    private Long projectId;
    private GeoJsonLineString userRouteCoordinate;

    public static UserRouteDetail createUserRouteDetail(ProjectStopRouteRequest request) {
        return UserRouteDetail.builder()
                .userId(request.getUserId())
                .projectId(request.getProjectId())
                .userRouteCoordinate(new GeoJsonLineString(request.getUserMovePath()))
                .build();
    }

}
