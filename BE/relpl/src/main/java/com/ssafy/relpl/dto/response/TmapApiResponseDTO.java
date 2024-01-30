package com.ssafy.relpl.dto.response;

import lombok.Data;

import java.util.Arrays;

@Data
public class TmapApiResponseDTO {

    public ResultData resultData;

    @Data
    public static class ResultData {

        public Header header;
        public LinkPoint[] linkPoints;

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

        public int laneType;
        public int tollLink;
        public int speed;
        public String roadName;
        public int oneway;
        public int roadCategory;
        public Long tlinkId;
        public Long linkId;
        public int linkFacil;
        public String idxName;
        public int totalDistance;
        public int lane;

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