package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Getter
@Setter
public class ProjectStopRequest {
    private Long userId;
    private Long projectId;
    private String userNickname;
    private String projectName;
    private String userMoveStart;
    private String userMoveEnd;
    private String userMoveTime;
    private int userMoveDistance;
    private List<Point> userMovePath;
    private String userMoveMemo;
    private String userMoveImage;
}
