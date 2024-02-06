package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.geo.Point;

import java.util.List;

@Getter
@Setter
@ToString

public class ProjectStopRouteRequest {
    private Long userId;
    private Long projectId;
    private String userNickname;
    private String projectName;
    private String moveStart;
    private String moveEnd;
    private int moveDistance;
    private int moveTime;
    private List<Point> userMovePath;
    private String moveMemo;
    private int projectCoordinateCurrentSize;
}
