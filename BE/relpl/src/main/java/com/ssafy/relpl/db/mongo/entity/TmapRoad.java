package com.ssafy.relpl.db.mongo.entity;

import com.ssafy.relpl.dto.response.TmapApiResponse;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
@Document(collection = "tmaproad")
public class TmapRoad {

    @Id
    private String id;
    private Long tmapId; // link_id
    private String roadName;
    private GeoJsonLineString geometry;
    private int lanetype;
    private int speed;
    private int totalDistance;
    private int lane;
    private Long roadHash;

    public static TmapRoad createRoad(TmapApiResponse responseDTO, Long roadHash) {


        TmapApiResponse.LinkPoint[] coordinates = responseDTO.getResultData().getLinkPoints();

        // List<List<Double>>를 List<GeoJsonPoint>로 변환
        List<Point> geoJsonPoints = Arrays.stream(coordinates)
                .map(coordinate -> new Point(coordinate.getLocation().getLatitude(), coordinate.getLocation().getLongitude()))
                .collect(Collectors.toList());

        // List<GeoJsonPoint>로 GeoJsonLineString 생성
        GeoJsonLineString geoJsonLineString = new GeoJsonLineString(geoJsonPoints);

        return TmapRoad.builder()
                .tmapId(responseDTO.getResultData().getHeader().getLinkId())
                .roadName(responseDTO.getResultData().getHeader().getRoadName())
                .geometry(geoJsonLineString)
                .lanetype(responseDTO.getResultData().getHeader().getLaneType())
                .speed(responseDTO.getResultData().getHeader().getSpeed())
                .totalDistance(responseDTO.getResultData().getHeader().getTotalDistance())
                .lane(responseDTO.getResultData().getHeader().getLane())
                .roadHash(roadHash)
                .build();
    }
}
