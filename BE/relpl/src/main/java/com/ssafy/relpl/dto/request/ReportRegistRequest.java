package com.ssafy.relpl.dto.request;

import com.ssafy.relpl.db.postgre.entity.Report;
import com.ssafy.relpl.db.postgre.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

@Getter
@Setter
public class ReportRegistRequest {

    private Long userId;
    private String reportDate;
    private Point reportCoordinate;
}
