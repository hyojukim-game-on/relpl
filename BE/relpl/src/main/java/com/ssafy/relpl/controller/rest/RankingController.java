package com.ssafy.relpl.controller.rest;


import com.ssafy.relpl.dto.response.CommonResponse;
import com.ssafy.relpl.dto.response.RankingDataDto;
import com.ssafy.relpl.dto.response.RankingResponse;
import com.ssafy.relpl.service.RankingService;
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

    @GetMapping("/{rankingTime}")
    public ResponseEntity<CommonResponse<RankingDataDto>> getRanking(@PathVariable String rankingTime) {
        CommonResponse<RankingDataDto> result = rankingService.getRanking(rankingTime);
        return ResponseEntity.ok(result);
    }
}