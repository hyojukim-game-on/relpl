package com.ssafy.relpl.business;

import com.ssafy.relpl.db.postgre.entity.PointHash;
import com.ssafy.relpl.db.postgre.entity.RoadInfo;
import com.ssafy.relpl.db.postgre.repository.PointHashRepository;
import com.ssafy.relpl.service.RoadInfoService;
import com.ssafy.relpl.util.annotation.Business;
import com.ssafy.relpl.util.common.Edge;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Business
@RequiredArgsConstructor
public class ProjectRecommendBusiness {

    private final RoadInfoService roadService;
    private final PointHashRepository pointHashRepository;

    List<RoadInfo> roadInfos;

    /**
     * 필요한 것
     * RoadInfo 테이블의 시작 좌표, 종료 좌표, 길이, 가중치
     * 1. 해당 도로와 다익스트라를 이용해 최단거리 계산
     * 2. 역방향 BFS를 통해 최단 도로 계산
     * --------------------------------
     * @return
     */

    @Transactional
    public ResponseEntity<?> recommendRoad(Point start, Point end) {
        if (pointHashMap == null) {
            initSettings();
            insertData();
        }
        commonSetting();



        return ResponseEntity.ok(true);
    }


    // algorithm
    ArrayList<Edge>[] edges;
    int vertexCnt = -1; // 꼭짓점 수
    Long[] minCost;
    HashMap<Long, Point> pointHashMap;
    boolean visit[];
    public void initSettings() {
        List<PointHash> pointHashList = pointHashRepository.findAll();
        for (PointHash pointHash : pointHashList) {
            pointHashMap.put(pointHash.getPointHashId(), pointHash.getPointCoordinate());
        }
        vertexCnt = pointHashMap.size();
        minCost = new Long[vertexCnt];
        edges = new ArrayList[vertexCnt];
        for (int i = 0; i < vertexCnt; i++) {
            edges[i] = new ArrayList<>();
        }
    }

    public void insertData() {
        roadInfos = roadService.getRoadInfo();
        for (RoadInfo roadInfo: roadInfos) {
            int start = Math.toIntExact(roadInfo.getPointHashIdStart());
            int end = Math.toIntExact(roadInfo.getPointHashIdStart());

            edges[start].add(new Edge(end, roadInfo.getRoadInfoLen()));
            edges[end].add(new Edge(start, roadInfo.getRoadInfoLen()));
        }
    }

    public void commonSetting() {
        visit = new boolean[vertexCnt];
        for (int i = 0; i < vertexCnt; i++) {
            minCost[i] = Long.MAX_VALUE;
        }
    }



}
