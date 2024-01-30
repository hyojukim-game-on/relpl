package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.dto.response.SampleResponseDto;
import com.ssafy.relpl.dto.response.SampleResponseDto2;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/sample")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SampleController {


    private final UserService userService;
    private final ResponseService responseService;

//    @GetMapping(value = "/save")
//    public User saveUser(@RequestParam String name, @RequestParam int age) {
//        return userService.save(name, age);
//    }

    // 나중에 이미지 관련 용도로 쓸듯 MultipartFile
//    @PostMapping("/upload/{userId}/{genre}")
//    public ResponseEntity<String> addMusic(@PathVariable String userId, @PathVariable String genre, @RequestPart("file") MultipartFile multipartFile) {

    @GetMapping("/get/{path1}/{path2}")
    public ResponseEntity<?> getSample(@PathVariable String path1, @PathVariable String path2) {

        SingleResult<SampleResponseDto> result = new SingleResult<>();
         responseService.getSingleResult("data", "OK", 200);
        result.setCode(200);
        result.setMessage("뭔가뭔가 성공");
        result.setData(SampleResponseDto
                .builder()
                .test1(path1)
                .test2(path2)
                .build());
//        return ResponseEntity.ok(result);
        return ResponseEntity.ok(responseService.getSingleResult(
                SampleResponseDto.builder()
                .test1(path1)
                .test2(path2)
                .build()));
    }


    @PostMapping(value = "/post")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        SingleResult<SampleResponseDto2> result = new SingleResult<>();
        result.setCode(400);
        result.setMessage("뭔가뭔가 실패");
        return ResponseEntity.badRequest().body(result);
    }
}
