package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.dto.request.CoinBarcodeRequestDto;
import com.ssafy.relpl.dto.request.CoinScoreRequestDto;
import com.ssafy.relpl.dto.response.*;
import com.ssafy.relpl.service.CoinService;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/user/mypage")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CoinController {


    private final UserService userService;
    private final CoinService coinService;
    private final ResponseService responseService;

    /**
     * @param coinscoreRequestDto
     * @return
     */
    @PostMapping(value = "/coinscore")
    // 포인트 내역 조회 로직
    public ResponseEntity<?> checkCoin(@RequestBody CoinScoreRequestDto coinscoreRequestDto) {

        try {
            log.info("여기는 컨트롤러. 포인트 내역 조회 요청 받음");
            coinService.completeProject(coinscoreRequestDto.getUserId());
            coinService.completePlogging(coinscoreRequestDto.getUserId());
            coinService.reportLocationFlag(coinscoreRequestDto.getUserId());

            // 사용자 코인 정보 조회
            CoinScoreDataResponseDto coinScoreDataResponseDto = coinService.getCoinScoreData(coinscoreRequestDto.getUserId());

            // 코인 데이터 삽입 예시
            coinService.insertCoinData(coinscoreRequestDto.getUserId(), "EVENT_123", "2024-01-24 12:30", 100, "Sample Event Detail");

            // 로오직
            SingleResult<CoinScoreDataResponseDto> result = responseService.getSingleResult(coinScoreDataResponseDto, "포인트 내역 조회 성공입니둥", 200);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("포인트 내역 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "포인트 내역 조회 실패입니둥", null));
        }
    }

    // 포인트 바코드 조회 로직
    @PostMapping(value = "/coinbarcode")
    public ResponseEntity<?> checkBarcode(@RequestBody CoinBarcodeRequestDto coinbarcodeRequestDto) {
        try {
            // 바코드 조회 로직 작성

            // 예시 결과 데이터
            CoinBarcodeResponseDto barcodeResponseDto = new CoinBarcodeResponseDto();
//            barcodeResponseDto.setBarcode("123456789");

            // 로직
            SingleResult<CoinBarcodeResponseDto> result = responseService.getSingleResult(barcodeResponseDto, "바코드 조회 성공", 200);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("바코드 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "바코드 조회 실패", null));
        }

    }
}



