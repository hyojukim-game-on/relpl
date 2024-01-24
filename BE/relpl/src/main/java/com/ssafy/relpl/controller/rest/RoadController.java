package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.SampleRequestDto;
import com.ssafy.relpl.service.RoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/road")
@CrossOrigin("*")
public class RoadController {

    @Autowired
    RoadService roadService;

//    @PostMapping(value = "/save")
//    public Road saveRoad(@RequestBody RoadRequest request) {
//        return roadService.save(request);
//    }

    @PostMapping(value = "/save")
    public SampleRequestDto.Road saveRoad(@RequestBody SampleRequestDto.RoadInfoRequest request) {
        return roadService.save(request);
    }
}
