package com.ssafy.relpl.dto.response;

import lombok.Data;

import java.util.Arrays;

@Data
public class TmapApiResponseDTO {

    private ResultData resultData;

    @Data
    public static class ResultData {

        private Header header;
        private LinkPoint[] linkPoints;

        @Override
        public String toString() {
            return "ResultData{" +
                    "header=" + header +
                    ", linkPoints=" + Arrays.toString(linkPoints) +
                    '}';
        }
    }

    @Data
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

        @Override
        public String toString() {
            return "Header{" +
                    "laneType=" + laneType +
                    ", tollLink=" + tollLink +
                    ", speed=" + speed +
                    ", roadName='" + roadName + '\'' +
                    ", oneway=" + oneway +
                    ", roadCategory=" + roadCategory +
                    ", tlinkId='" + tlinkId + '\'' +
                    ", linkId='" + linkId + '\'' +
                    ", linkFacil=" + linkFacil +
                    ", idxName='" + idxName + '\'' +
                    ", totalDistance=" + totalDistance +
                    ", lane=" + lane +
                    '}';
        }
    }

    @Data
    public static class LinkPoint {

        private Location location;

        @Override
        public String toString() {
            return "LinkPoint{" +
                    "location=" + location +
                    '}';
        }
    }

    @Data
    public static class Location {

        private double latitude;
        private double longitude;

        @Override
        public String toString() {
            return "Location{" +
                    "latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TmapApiResponseDTO{" +
                "resultData=" + resultData +
                '}';
    }
}