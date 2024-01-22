package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.User;
import com.ssafy.relpl.dto.request.CoinscoreRequestDto;
import com.ssafy.relpl.dto.response.CoinscoreResponseDto;
import com.ssafy.relpl.dto.response.CoinscoreResponseDto2;
import com.ssafy.relpl.dto.response.CommonResponse;
import com.ssafy.relpl.dto.response.CoinscoreDataResponseDto;
import com.ssafy.relpl.dto.response.SampleResponseDto;
import com.ssafy.relpl.dto.response.SampleResponseDto2;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/user") // 흠??
@RequiredArgsConstructor
@CrossOrigin("*")
public class CoinscoreController {

    private final UserService userService; //??
    private final ResponseService responseService; //??

    private final CoinService coinService; //

//    @GetMapping(value = "/save")
//    public User saveUser(@RequestParam String name, @RequestParam int age) {
//        return userService.save(name, age);
//    }

    // 나중에 이미지 관련 용도로 쓸듯 MultipartFile
//    @PostMapping("/upload/{userId}/{genre}")
//    public ResponseEntity<String> addMusic(@PathVariable String userId, @PathVariable String genre, @RequestPart("file") MultipartFile multipartFile) {

    // 성공시
    @PostMapping(value = "/mypage/coinscore")
    public ResponseEntity<?> getSample(@RequestBody CoinscoreRequestDto coinscoreRequestDto) {

        SingleResult<CoinscoreResponseDto> result = new SingleResult<>();
        result.setCode(200);
        result.setMessage("포인트 내역 조회 성공");
        result.setData(CoinscoreResponseDto
                .coinEventDate()
                .coinAmount()
                .coinEventDetail());
        return ResponseEntity.ok(result);
//        return ResponseEntity.ok(responseService.getSingleResult(
//                SampleResponseDto.builder()
//                .test1(path1)
//                .test2(path2)
//                .build()));
    }

    // 실패시
    @PostMapping(value = "/post")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        SingleResult<CoinscoreResponseDto2> result = new SingleResult<>();
        result.setCode(400);
        result.setMessage("포인트 내역 조회 실패");
        return ResponseEntity.badRequest().body(result);
    }
}
