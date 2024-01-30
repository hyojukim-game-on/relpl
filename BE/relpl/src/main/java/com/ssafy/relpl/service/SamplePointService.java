package com.ssafy.relpl.service;

import com.ssafy.relpl.dto.request.SamplePoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.geo.Point;

public class SamplePointService {

    
    // requestDTO 의 SamplePoint 를 Coordinate 로 변환하기
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    Point start = SamplePoint.getReportCoordinate();
    Coordinate coordinate = new Coordinate(start.getX(), start.getY());
        report.setReportCoordinate(geometryFactory.createPoint(coordinate));

}
