package com.ssafy.relpl.dto.request;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class InsertRoadRequestDto {
    private Point startPoint;
    private Point endPoint;
    private String callApiPassword;
}
