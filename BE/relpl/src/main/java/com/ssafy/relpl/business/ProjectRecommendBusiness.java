package com.ssafy.relpl.business;

import com.ssafy.relpl.config.GeomFactoryConfig;
import com.ssafy.relpl.db.mongo.entity.RecommendProject;
import com.ssafy.relpl.db.mongo.entity.TmapRoad;
import com.ssafy.relpl.db.postgre.entity.PointHash;
import com.ssafy.relpl.db.postgre.entity.RoadHash;
import com.ssafy.relpl.db.postgre.entity.RoadInfo;
import com.ssafy.relpl.service.*;
import com.ssafy.relpl.util.annotation.Business;
import com.ssafy.relpl.util.common.Edge;
import com.ssafy.relpl.util.common.Info;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.http.ResponseEntity;

import java.util.*;

@Business
@Slf4j
@RequiredArgsConstructor
public class ProjectRecommendBusiness {
// for committt

    private final RoadInfoService roadService;
    private final PointHashService pointHashService;
    private final TmapRoadService tmapRoadService;
    private final RecommendProjectService recommendProjectService;

    private final GeomFactoryConfig geomFactoryConfig;
    List<RoadInfo> roadInfos;


    /**
     * 해야하는 것
     * 0. 만약 최적경로 제공을 위한 세팅이 안되있을 경우 값을 RoadInfo DB에서 가져오기
     * 1. 입력된 시작점, 끝 점으로 부터 가장 가까운 시작점과 끝 점을 RoadHash DB에서 검색
     * 2. 해당 점을 가지고 다익스트라 알고리즘 진행 => 결과값은 시작 점으로부터 모든 점 까지의 최단거리
     * 3. 다익스트라로 나온 최단 거리를 바탕으로 역방향 BFS를 진행해서 최단 경로를 계산
     * 3-1. 3번의 결과값으로 나온 도로의 결과는 hashing된 도로 번호
     * 4. roadHash Table에서 Tmap Id값을 가져옴
     * 5. 4번의 결과값을 가지고 PostgreSQL의 roadHash 테이블에서 MongoDB에서 실제 결과값을 가져옴
     * 6. 가져온 결과값의 앞 뒤에 시작점 끝점을 붙여서 추천 경로 테이블에 insert
     * 7. 6번의 결과값을 return
     * @param start 시작점
     * @param end 끝 점
     * @return
     */
    @Transactional
    public ResponseEntity<?> recommendRoad(Point start, Point end) {

        // 0번
        if (pointHashMap == null) {
            initSettings();
            initArrayList();
        }

        // 1번
        PointHash realStartHash = pointHashService.getNearPoint(start.getX(), start.getY());
        PointHash realEndHash = pointHashService.getNearPoint(end.getX(), end.getY());
        log.info("realStartHash: {}, realEndHash: {}", realStartHash.toString(), realEndHash.toString());
        // 2번
        long[] shortestCost = dijkstraShortestPath(Math.toIntExact(realStartHash.getPointHashId()), Math.toIntExact(realEndHash.getPointHashId()));
//        Long[] recommendCost = dijkstraRecommendPath(Math.toIntExact(realStartHash.getPointHashId()), Math.toIntExact(realEndHash.getPointHashId()));
        log.info("dijkstraShortestPath 완료");

        // 3번
        List<Long> shortestRoadHash = getShortestRoadHashRevBfs(Math.toIntExact(realStartHash.getPointHashId()), Math.toIntExact(realEndHash.getPointHashId()), shortestCost);
        log.info("getShortestRoadHashRevBfs 완료");

//        List<Long> shortestTmapRoadId = roadHashToTmapRoad(shortestRoadHash);
//        log.info("shortestTmapRoadId 완료");

        List<TmapRoad> shortestTmapRoad = tmapRoadService.getAllTmapRoadById(shortestRoadHash);
        log.info("shortestTmapRoad 완료");

        HashSet<org.springframework.data.geo.Point> shortestPoints = new HashSet<>();

            shortestPoints.add(
                    new org.springframework.data.geo.Point(
                            start.getCoordinate().getX(), start.getCoordinate().getY()
                    )
            );

        for (TmapRoad tmapRoad : shortestTmapRoad) {
            for (org.springframework.data.geo.Point point: tmapRoad.getGeometry().getCoordinates()) {
                shortestPoints.add(
                        new org.springframework.data.geo.Point(point.getX(), point.getY()
                        )
                );
            }
        }

        shortestPoints.add(new org.springframework.data.geo
                .Point(end.getCoordinate().getX(), end.getCoordinate().getY()
                )
        );
        RecommendProject shortestProject = recommendProjectService.saveRecommendProject(new ArrayList<>(shortestPoints));
        return ResponseEntity.ok(shortestProject);
    }


    // ------------------------------------------------ 경로 추천 알고리즘 관련
    ArrayList<Edge>[] shortestEdges, recommendEdges;
    int vertexCnt = -1; // 꼭짓점 수
    HashMap<Long, Point> pointHashMap;

    /**
     * DB에서 값을 가져와서 다익스트라를 위한 ArrayList에 값을 넣어주는 함수
     */
    public void initSettings() {

        List<PointHash> pointHashList = pointHashService.getAllPointHash();
        pointHashMap = new HashMap<>();
        for (PointHash pointHash : pointHashList) {
            pointHashMap.put(pointHash.getPointHashId(), pointHash.getPointCoordinate());
        }
        vertexCnt = pointHashMap.size();
        shortestEdges = new ArrayList[vertexCnt];
        recommendEdges = new ArrayList[vertexCnt];
        for (int i = 0; i < vertexCnt; i++) {
            shortestEdges[i] = new ArrayList<>();
            recommendEdges[i] = new ArrayList<>();
        }
    }

    /**
     * DB에서 가져온 값을 바탕으로 다익스트라를 위해 ArrayList에 값을 넣어주는 함수
     */
    public void initArrayList() {
        roadInfos = roadService.getRoadInfo();
        log.info("initArrayList()");
        for (RoadInfo roadInfo: roadInfos) {
            int start = Math.toIntExact(roadInfo.getPointHashIdStart());
            int end = Math.toIntExact(roadInfo.getPointHashIdEnd());
            log.info("start: {}, end: {}, length: {}", start, end, roadInfo.getRoadInfoLen());
            shortestEdges[start].add(new Edge(end, roadInfo.getRoadInfoLen(), roadInfo.getRoadHashId()));
            shortestEdges[end].add(new Edge(start, roadInfo.getRoadInfoLen(), roadInfo.getRoadHashId()));
//            recommendEdges[start].add(new Edge(end, roadInfo.getRoadInfoLen(), roadInfo.getRoadInfoWeight()));
//            recommendEdges[end].add(new Edge(start, roadInfo.getRoadInfoLen(), roadInfo.getRoadInfoWeight()));
        }
    }
// ----------------------------------------------------------------
    /**
     * 다익스트라 최단경로 결과값
     * @param start
     * @return start로부터의 모든 점 까지의 최단거리 Long[]
     */
    public long[] dijkstraShortestPath(int start, int end) {
        boolean[] visit = new boolean[vertexCnt];
        long[] cost = new long[vertexCnt];
        for (int i = 0; i < vertexCnt; i++) {
            cost[i] = Integer.MAX_VALUE;
        }
        cost[start] = 0L;
        PriorityQueue<Info> pq = new PriorityQueue<>((o1, o2) -> Long.compare(o1.distSum, o2.distSum));
        pq.add(new Info(start, 0L));
        log.info("start");
        while (!pq.isEmpty()) {
            Info info = pq.poll();
            if (visit[info.cur]) continue;
            if (info.cur == end) break;
            visit[info.cur] = true;
            for (int i = 0; i < shortestEdges[info.cur].size(); i++) {
                Edge next = shortestEdges[info.cur].get(i);
                if (cost[next.to] < cost[info.cur] + next.dist) continue;
                cost[next.to] = cost[info.cur] + next.dist;
                pq.add(new Info(next.to, cost[next.to]));
            }
        }
        return cost;
    }

// ----------------------------------------------------------------
    /**
     * 다익스트라 추천경로 결과값
     * @param start
     * @return start로부터의 모든 점 까지의 최단거리 Long[]
     */
    public long[] dijkstraRecommendPath(int start, int end) {
        boolean[] visit = new boolean[vertexCnt];
        long[] cost = new long[vertexCnt];
        for (int i = 0; i < vertexCnt; i++) {
            cost[i] = Integer.MAX_VALUE;
        }
        cost[start] = 0L;
        PriorityQueue<Info> pq = new PriorityQueue<>((o1, o2) -> Long.compare(o1.distSum, o2.distSum));
        pq.add(new Info(start, 0L));
        log.info("start");
        while (!pq.isEmpty()) {
            Info info = pq.poll();
            if (visit[info.cur]) continue;
            if (info.cur == end) break;
            visit[info.cur] = true;
            for (int i = 0; i < recommendEdges[info.cur].size(); i++) {
                Edge next = recommendEdges[info.cur].get(i);
                if (cost[next.to] < cost[info.cur] + next.dist) continue;
                cost[next.to] = cost[info.cur] + next.dist;
                pq.add(new Info(next.to, cost[next.to]));
            }
        }
        return cost;
    }
// ----------------------------------------------------------------
    /**
     * 역방향 bfs를 통해 앞에서 계산된 최단거리를 기반으로 최단 경로 확인
     * @param end 끝 점
     * @param shortestCost 시작점으로부터 모든 점 까지의 최단경로 Long[]
     * @return hash값이 적용된 road값이 반환, roadHash를 이용해 변환 필요
     */
    public List<Long> getShortestRoadHashRevBfs(int start, int end, long[] shortestCost) {

        List<Long> pathRoadsHash = new ArrayList<>();
        boolean[] visit = new boolean[vertexCnt];
        visit[end] = true;

        Deque<Integer> que = new ArrayDeque<>();
        que.add(end);

        while (!que.isEmpty()) {
            int cur = que.poll();
            if (cur == start) break;
            for (Edge next: shortestEdges[cur]) {
                if (visit[next.to]) continue;
                if (shortestCost[cur] - next.dist == shortestCost[next.to]) {
                    pathRoadsHash.add(next.roadHash);
                    visit[next.to] = true;
                    que.add(next.to);
                }
            }
        }
        return pathRoadsHash;
    }
// ----------------------------------------------------------------
    /**
     * hash값이 저장된 road값을 tmap api 제공 id값으로 변환하는 함수
     * @param pathRoadsHash hashing된 road 번호들의 list
     * @return hash값이 없어진 tmap api 제공 id로 변환된 List
     */
//    public List<Long> roadHashToTmapRoad(List<Integer> pathRoadsHash) {
//
//        HashMap<Long,Long> roadHashMap = new HashMap<Long,Long>();
//        List<RoadHash> roadHashList = roadHashService.getAllRoadHash();
//        for (RoadHash roadHash : roadHashList) {
//            roadHashMap.put(roadHash.getRoadHashId(), roadHash.getTmapId());
//        }
//
//        List<Long> tmapRoadList = new ArrayList<>();
//        for (int road : pathRoadsHash) {
//            tmapRoadList.add(roadHashMap.get(road));
//        }
//
//        return tmapRoadList;
//    }
// ----------------------------------------------------------------
}
