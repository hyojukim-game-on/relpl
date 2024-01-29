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

    // 수정부분
    private Long tmapId; // tmapId 변수를 추가


    public Long getTmapId() {
        return tmapId;
    }

    public void setTmapId(Long tmapId) {
        this.tmapId = tmapId;
    }

}
