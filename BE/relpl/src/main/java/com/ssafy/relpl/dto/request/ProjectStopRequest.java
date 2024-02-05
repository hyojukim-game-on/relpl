package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProjectStopRequest {
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
    private MultipartFile moveImage;
}
