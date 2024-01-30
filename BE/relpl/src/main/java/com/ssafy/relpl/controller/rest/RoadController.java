package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.business.ProjectRecommendBusiness;
import com.ssafy.relpl.dto.request.ProjectRecommendRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/project")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RoadController {

    private final ProjectRecommendBusiness projectRecommendBusiness;

    @PostMapping("/recommend")
    public ResponseEntity<?> recommendProject(@RequestBody ProjectRecommendRequestDto request) {
        return projectRecommendBusiness.recommendRoad();
    }
}
