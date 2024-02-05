package com.ssafy.relpl.dto.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;
import java.util.List;

@Data
@Builder
public class ProjectRecommendResponseDto {
    String shortestId;
    List<Point> shortestPath;
    String recommendId;
    List<Point> recommendPath;
}
