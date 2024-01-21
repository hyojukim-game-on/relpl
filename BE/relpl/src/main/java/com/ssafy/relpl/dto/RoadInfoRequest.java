package com.ssafy.relpl.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoadInfoRequest {

    private ResultData resultData;

    @Getter
    @Setter
    public static class ResultData {
        private Header header;
        private List<LinkPoint> linkPoints;
    }

    @Getter
    @Setter
    public static class Header {
        private int laneType;
        private int tollLink;
        private int speed;
        private String roadName;
        private int oneway;
        private int roadCategory;
        private String tlinkId;
        private String linkId;
        private int linkFacil;
        private String idxName;
        private int totalDistance;
        private int lane;
    }

    @Getter
    @Setter
    public static class LinkPoint {
        private Location location;
    }

    @Getter
    @Setter
    public static class Location {
        private double latitude;
        private double longitude;
    }
}
