package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.CoinBarcodeRequestDto;
import com.ssafy.relpl.dto.request.CoinScoreRequestDto;
import com.ssafy.relpl.dto.response.CoinBarcodeResponseDto;
import com.ssafy.relpl.dto.response.CoinScoreDataResponseDto;
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
    // 포인트 내역 조회 로직 메서드
    @PostMapping(value = "/coinscore")
    public ResponseEntity<?> checkCoin(@RequestBody CoinScoreRequestDto coinscoreRequestDto) {
        try {
            log.info("여기는 컨트롤러. 포인트 내역 조회 요청 받음");

            // 유저 정보 확인
            Long userId = coinscoreRequestDto.getUserId();
            SingleResult<CoinScoreDataResponseDto> result = coinService.coinScore(userId);

            // 내역 조회 성공
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            // 내역 조회 실패
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.getFailResult(400, "등록되지 않은 ID입니다. 포인트 내역 조회에 실패하였습니다."));
        }
    }

    // 포인트 바코드 조회 로직
    @PostMapping(value = "/coinbarcode")
    public ResponseEntity<?> checkCoinBarcode(@RequestBody CoinBarcodeRequestDto coinBarcodeRequestDto) {
        try {
            log.info("여기는 컨트롤러. 포인트 바코드 조회 요청 받음");

            // 유저 정보 확인
            Long userId = coinBarcodeRequestDto.getUserId();
            SingleResult<CoinBarcodeResponseDto> result = coinService.coinBarcode(userId);

            // 바코드 조회 성공
            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            // 바코드 조회 실패
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(responseService.getFailResult(400, "등록되지 않은 ID입니다. 포인트 바코드 조회에 실패하였습니다."));
        }
    }
}
