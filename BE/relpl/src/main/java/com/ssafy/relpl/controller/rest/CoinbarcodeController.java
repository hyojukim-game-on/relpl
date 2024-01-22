package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.User;
import com.ssafy.relpl.dto.request.CoinbarcodeRequestDto;
import com.ssafy.relpl.dto.response.CoinbarcodeResponseDto;
import com.ssafy.relpl.dto.response.CoinbarcodeResponseDto2;
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
@RequestMapping(path = "/api/user")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CoinbarcodeController {


    private final UserService userService;
    private final ResponseService responseService;

//    private final CoinbarcodeService coinbarcodeService;
//
//    @GetMapping(value = "/save")
//    public User saveUser(@RequestParam String name, @RequestParam int age) {
//        return userService.save(name, age);
//    }

    // 나중에 이미지 관련 용도로 쓸듯 MultipartFile
//    @PostMapping("/upload/{userId}/{genre}")
//    public ResponseEntity<String> addMusic(@PathVariable String userId, @PathVariable String genre, @RequestPart("file") MultipartFile multipartFile) {

    // 성공시
    @PostMapping(value = "/mypage/coinbarcode")
    public ResponseEntity<?> getSample(@RequestBody CoinbarcodeRequestDto coinbarcodeRequestDto) {

        SingleResult<CoinbarcodeResponseDto> result = new SingleResult<>();
        result.setCode(200);
        result.setMessage("포인트 바코드 조회 성공");
        result.setData(CoinbarcodeResponseDto
                .userTotalCoin());
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
        SingleResult<CoinbarcodeResponseDto2> result = new SingleResult<>();
        result.setCode(400);
        result.setMessage("포인트 조회 실패");
        return ResponseEntity.badRequest().body(result);
    }
}
