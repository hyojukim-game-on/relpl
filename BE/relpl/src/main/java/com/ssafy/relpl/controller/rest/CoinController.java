package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.dto.request.CoinBarcodeRequestDto;
import com.ssafy.relpl.dto.request.CoinScoreRequestDto;
import com.ssafy.relpl.dto.response.CoinBarcodeResponseDto;
import com.ssafy.relpl.dto.response.CoinScoreResponseDto;
import com.ssafy.relpl.dto.response.SampleResponseDto;
import com.ssafy.relpl.dto.response.SampleResponseDto2;
import com.ssafy.relpl.service.CoinService;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     *
     * @param coinscoreRequestDto
     * @return
     */
    @PostMapping(value = "/coinscore")
    // 겟 샘플그대로 쓰지 마라.. 뭐하는 함수인지도 간략하게 작성.
    public ResponseEntity<?> checkCoin(@RequestBody CoinScoreRequestDto coinscoreRequestDto) {

        SingleResult<CoinScoreResponseDto> result = new SingleResult<>();
        result.setCode(200);
        log.info(coinscoreRequestDto.getUserId().toString());

        result.setMessage("포인트 내역 조회 성공");
//        result.setData(CoinScoreResponseDto
//                this.coinEventDate()
//                this.coinAmount()
//                this.coinEventDetail());
        // 문법오류

        return ResponseEntity.ok(result);
    }


    // 성공시
    @PostMapping(value = "/coinbarcode")
    public ResponseEntity<?> checkCoinBarcode(@RequestBody CoinBarcodeRequestDto coinbarcodeRequestDto) {

        SingleResult<CoinBarcodeResponseDto> result = new SingleResult<>();
        result.setCode(200);
        result.setMessage("포인트 바코드 조회 성공");
//        result.setData(CoinBarcodeResponseDto
//                .userTotalCoin()); // 서비스가 줘야한다. 아무것도 없는값에 접근하는중. 어떻게 조회하는지에 대한 공부 필요.
//문법 오류


        return ResponseEntity.ok(result);
//        return ResponseEntity.ok(responseService.getSingleResult(
//                SampleResponseDto.builder()
//                .test1(path1)
//                .test2(path2)
//                .build()));
    }

    // TODO
    // 실패시 처리 로직 필요
    @PostMapping(value = "/post")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        SingleResult<CoinBarcodeResponseDto> result = new SingleResult<>();
        result.setCode(400);
        result.setMessage("포인트 조회 실패");
        return ResponseEntity.badRequest().body(result);
    }
}
