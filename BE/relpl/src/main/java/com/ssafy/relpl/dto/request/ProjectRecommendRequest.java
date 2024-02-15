package com.ssafy.relpl.dto.request;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class ProjectRecommendRequest {

    Point startCoordinate;
    Point endCoordinate;
}
