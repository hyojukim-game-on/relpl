package com.ssafy.relpl.dto.response;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

@Data
@Builder
public class ReportListResponseDto {

    private String reportDate; // yyyy-mm-dd
    private Point reportCoordinate;


}
