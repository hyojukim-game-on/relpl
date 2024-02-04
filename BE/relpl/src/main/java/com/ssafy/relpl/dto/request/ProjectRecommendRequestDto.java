package com.ssafy.relpl.dto.request;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class ProjectRecommendRequestDto {

    Point startPoint;
    Point endPoint;
}
