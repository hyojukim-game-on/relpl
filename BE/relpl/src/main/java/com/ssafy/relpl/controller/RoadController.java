package com.ssafy.relpl.controller;

import com.ssafy.relpl.dto.Road;
import com.ssafy.relpl.dto.RoadInfoRequest;
import com.ssafy.relpl.service.RoadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/road")
public class RoadController {

    @Autowired
    RoadService roadService;

//    @PostMapping(value = "/save")
//    public Road saveRoad(@RequestBody RoadRequest request) {
//        return roadService.save(request);
//    }

    @PostMapping(value = "/save")
    public Road saveRoad(@RequestBody RoadInfoRequest request) {
        return roadService.save(request);
    }
}
