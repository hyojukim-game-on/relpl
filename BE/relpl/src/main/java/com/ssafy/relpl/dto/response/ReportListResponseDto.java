package com.ssafy.relpl.dto.response;


import com.ssafy.relpl.util.common.LatLng;
import lombok.Data;

@Data
public class ReportResponseDto {

    private String reportDate; // yyyy-mm-dd
    private LatLng reportCoordinate; 


}
