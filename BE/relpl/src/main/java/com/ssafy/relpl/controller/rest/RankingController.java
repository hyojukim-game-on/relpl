package com.ssafy.relpl.controller.rest;


import com.ssafy.relpl.service.RankingService;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping(path = "api/rank")
@RequiredArgsConstructor
@CrossOrigin("*")
public class RankingController {

    private final RankingService rankingService;

//    @GetMapping("/{rankingTime}")
//    public ResponseEntity<CommonResult> getRanking(@PathVariable String rankingTime) {
//
//        return rankingService.getRanking(rankingTime);
//    }

    @GetMapping("/test/{nickname}/{distance}")
    public CommonResult testRanking(@PathVariable String nickname, @PathVariable double distance) {
        return rankingService.testRanking(nickname, distance);
    }


}