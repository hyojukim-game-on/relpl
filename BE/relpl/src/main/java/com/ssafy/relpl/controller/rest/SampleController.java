package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.dto.response.SampleResponse;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.TmapService;
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

    private final ResponseService responseService;
    private final TmapService tmapService;
    @GetMapping("/get/{path1}/{path2}")
    public ResponseEntity<?> getSample(@PathVariable String path1, @PathVariable String path2) {

        SingleResult<SampleResponse> result = new SingleResult<>();
         responseService.getSingleResult("data", "OK", 200);
        result.setCode(200);
        result.setMessage("뭔가뭔가 성공");
        result.setData(SampleResponse
                .builder()
                .test1(path1)
                .test2(path2)
                .build());
//        return ResponseEntity.ok(result);
        return ResponseEntity.ok(responseService.getSingleResult(
                SampleResponse.builder()
                .test1(path1)
                .test2(path2)
                .build()));
    }

    @PostMapping(value = "/post")
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        SingleResult<SampleResponse> result = new SingleResult<>();
        result.setCode(400);
        result.setMessage("뭔가뭔가 실패");
        return ResponseEntity.badRequest().body(result);
    }

    @GetMapping("/test")
    public String data() {
        return tmapService.callTmapApi(128.421290, 36.111525).toString();
    }
}
