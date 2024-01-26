package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter
@Setter
public class ReportRegistRequestDto {

    private Long userId;
    private String reportDate;
    private Point reportCoordinate;
}
