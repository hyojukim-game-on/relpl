package com.ssafy.relpl.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.relpl.db.mongo.entity.Road;
import com.ssafy.relpl.dto.response.TmapApiResponseDTO;
import com.ssafy.relpl.service.TmapService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoadController {
//    private final Logger log = LoggerFactory.getLogger(RoadController.class);
//    private final TmapService tmapService;
//
//    @GetMapping("/callTmapApiMultipleTimes")
//    public void callTmapApiMultipleTimes(@RequestParam double StartLat,
//                                         @RequestParam double StartLng,
//                                         @RequestParam double EndLat,
//                                         @RequestParam double EndLng) throws JsonProcessingException {
//
//        log.info("StartLat: " + StartLat + ", StartLng: " + StartLng + ", EndLat: " + EndLat + ", EndLng: " + EndLng);
//        //범위 안에 중복되지 않도록 Map 에 저장
//        HashMap<String, TmapApiResponseDTO> roads = new HashMap<>();
//        for (double lat = StartLat; lat >= EndLat; lat -= 0.0001) {
//            for (double lng = StartLng; lng <= EndLng; lng += 0.0001) {
//                //Tmap api 호출
//                String responseData = tmapService.callTmapApi(lat, lng);
////                System.out.println("API Call " + ": " + responseData);
//
//                // ObjectMapper를 생성하여 JSON 응답을 TmapApiResponseDTO 객체로 변환한다.
//                ObjectMapper objectMapper = new ObjectMapper();
//                TmapApiResponseDTO responseDTO = objectMapper.readValue(responseData, TmapApiResponseDTO.class);
//                log.info("lat: " + lat + ", lng: " + lng + ", API Call: " + responseDTO);
//
//                if(!roads.containsKey(responseDTO.getResultData().getHeader().getTlinkId())) {
//                    roads.put(responseDTO.getResultData().getHeader().getTlinkId(), responseDTO);
//                }
//            }
//        }
//        Long count = Long.valueOf(1);
//        for(String key : roads.keySet()) {
//            TmapApiResponseDTO road = roads.get(key);
//            tmapService.saveRoad(road);                //Road 저장하기
//            tmapService.saveRoadHash(count++, key);    //RoadHash 저장하기
//        }
//    }
//
////    @GetMapping("/callTmapApiMultipleTimes")
////    public void callTmapApiMultipleTimes(@RequestParam int numberOfCalls,
////                                         @RequestParam double latitude,
////                                         @RequestParam double longitude) throws JsonProcessingException {
////        for (int i = 0; i < numberOfCalls; i++) {
////            //Tmap api 호출
////            String responseData = tmapMultiCallService.callTmapApi(latitude, longitude);
////            System.out.println("API Call " + (i + 1) + ": " + responseData);
////
////            // ObjectMapper를 생성하여 JSON 응답을 TmapApiResponseDTO 객체로 변환한다.
////            ObjectMapper objectMapper = new ObjectMapper();
////            TmapApiResponseDTO responseDTO = objectMapper.readValue(responseData, TmapApiResponseDTO.class);
////            System.out.println("API Call " + (i + 1) + ": " + responseDTO);
////
////            //responseDTO 저장하기
////            tmapMultiCallService.saveRoad(responseDTO);
////        }
////    }
//
//    @GetMapping("/road/all")
//    public List<Road> getAllRoads() {
//        return tmapService.getAllRoads();
//    }
//
//    @GetMapping("/road/{id}")
//    public Road getAllRoads(@PathVariable String id) {
//        return tmapService.getRoad(id);
//    }
}
