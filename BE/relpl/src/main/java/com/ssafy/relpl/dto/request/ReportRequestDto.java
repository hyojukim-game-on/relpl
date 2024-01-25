package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter
@Setter
public class ReportRequestDto {

    private Long userId;
    private String reportDate;
    private Point reportCoordinate;
}

// ReportResponseDto 는 반환변수가 code와 message뿐이므로 굳이 만들지 않는다.
