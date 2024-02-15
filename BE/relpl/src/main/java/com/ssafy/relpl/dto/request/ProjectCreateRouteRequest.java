package com.ssafy.relpl.dto.request;

import lombok.Data;
import org.springframework.data.geo.Point;

@Data
public class ProjectCreateRouteRequest {
    Long userId;  // 프로젝트를 생성한 유저
    String projectSelectedId;// 경로 추천시 제공한 id(이전 제공값)
    int projectSelectedTotalDistance; // 프로젝트 전체 길이(이전 제공값)
    int projectSelectedCoordinateTotalSize; // 프로젝트 전체 위,경도 배열 크기

    String projectName;
    String projectCreateDate;
    String projectEndDate;
    Point projectStartCoordinate;
    Point projectEndCoordinate;
}
