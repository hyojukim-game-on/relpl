package com.ssafy.relpl.dto.response;



//import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ssafy.relpl.db.postgre.entity.Report;
import com.ssafy.relpl.serializer.CustomPointSerializer;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Data
public class ReportListResponse {

    private String reportDate; // yyyy-mm-dd


    //@JsonIgnore
    @JsonSerialize(using = CustomPointSerializer.class)
    private Point reportCoordinate;

    public ReportListResponse(Report report) {
        this.reportDate = report.getReportDate(); // Report 엔터티의 메서드를 사용하여 값을 가져오도록
        this.reportCoordinate = report.getReportCoordinate(); // Report 엔터티의 메서드를 사용하여 값을 가져오도록
    }

}