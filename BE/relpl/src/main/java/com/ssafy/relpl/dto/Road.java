package com.ssafy.relpl.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Document(collection = "road")
public class Road {
    private String tmap_id;
    private GeoJsonLineString geometry;
    private int lanetype;
    private int speed;
    private int total_distance;
    private int lane;

    private static final Logger logger = LoggerFactory.getLogger(Road.class);

    public static Road createRoad(RoadInfoRequest request) {

        List<RoadInfoRequest.LinkPoint> coordinates = request.getResultData().getLinkPoints();

        // List<List<Double>>를 List<GeoJsonPoint>로 변환
        List<Point> geoJsonPoints = coordinates.stream()
                .map(coordinate -> new Point(coordinate.getLocation().getLatitude(), coordinate.getLocation().getLongitude()))
                .collect(Collectors.toList());

        // List<GeoJsonPoint>로 GeoJsonLineString 생성
        GeoJsonLineString geoJsonLineString = new GeoJsonLineString(geoJsonPoints);

        return Road.builder()
                .tmap_id(request.getResultData().getHeader().getTlinkId())
                .geometry(geoJsonLineString)
                .lanetype(request.getResultData().getHeader().getLaneType())
                .speed(request.getResultData().getHeader().getSpeed())
                .total_distance(request.getResultData().getHeader().getTotalDistance())
                .lane(request.getResultData().getHeader().getLane())
                .build();
    }

//    public static Road createRoad(RoadRequest request) {
//
//        List<List<Double>> coordinates = request.getCoordinates();
//
//        // List<List<Double>>를 List<GeoJsonPoint>로 변환
//        List<Point> geoJsonPoints = coordinates.stream()
//                .map(coordinate -> new Point(coordinate.get(0), coordinate.get(1)))
//                .collect(Collectors.toList());
//
//
//        // List<GeoJsonPoint>로 GeoJsonLineString 생성
//        GeoJsonLineString geoJsonLineString = new GeoJsonLineString(geoJsonPoints);
//
//        return Road.builder()
//                .tmap_id(request.getTmap_id())
//                .geometry(geoJsonLineString)
//                .lanetype(request.getLanetype())
//                .speed(request.getSpeed())
//                .total_distance(request.getTotal_distance())
//                .lane(request.getLane())
//                .build();
//    }
}
