package com.ssafy.relpl.dto.request;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class ProjectCreateDistanceRequest {
    Long userId;
    String projectName;
    String projectCreateDate;
    String projectEndDate;
    int projectTotalDistance;
    Point projectStartCoordinate;
}
