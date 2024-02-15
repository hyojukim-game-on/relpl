package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.CoinBarcodeRequest;
import com.ssafy.relpl.dto.request.CoinScoreRequest;
import com.ssafy.relpl.dto.response.CoinBarcodeResponse;
import com.ssafy.relpl.dto.response.CoinScoreDataResponse;
import com.ssafy.relpl.service.CoinService;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user/mypage")
@RequiredArgsConstructor
public class CoinController {

    private final CoinService coinService;

    // 포인트 내역 조회 로직 메서드
    @PostMapping("/coinscore")
    public ResponseEntity<?> checkCoin(@RequestBody CoinScoreRequest coinscoreRequest) {
        log.info("여기는 컨트롤러. 포인트 내역 조회 요청 받음");

        // 유저 정보 확인
        Long userId = coinscoreRequest.getUserId();
        return coinService.coinScore(userId);
    }

    // 포인트 바코드 조회 로직
    @PostMapping("/coinbarcode")
    public ResponseEntity<?> checkCoinBarcode(@RequestBody CoinBarcodeRequest coinBarcodeRequest) {
        log.info("여기는 컨트롤러. 포인트 바코드 조회 요청 받음");
        return coinService.coinBarcode(coinBarcodeRequest.getUserId());
    }
}
