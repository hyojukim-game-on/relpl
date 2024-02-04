package com.ssafy.relpl.controller.rest;


import com.ssafy.relpl.service.RankingService;
import com.ssafy.relpl.service.result.CommonResult;
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

    /* getNowRanking : 일간/주간/월간랭킹 1 ~ 20 위 반환
     * @parameter : 없음
     * @return : 200 OK / 400 랭킹 조회 중 오류 발생
     * */
    @GetMapping("/now")
    public ResponseEntity<CommonResult> getNowRanking () {
        log.info("getNowRanking 내부로 들어옴");
        return rankingService.getNowRanking();
    }
}