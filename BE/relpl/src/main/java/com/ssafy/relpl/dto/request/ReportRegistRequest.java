package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter
@Setter
public class ReportRegistRequest {


    private Long userId;
    private String reportDate;
    private Point reportCoordinate;
    private Long tmapId; // tmapId 변수를 추가

}
