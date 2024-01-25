package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.ReportListRequestDto;
import com.ssafy.relpl.dto.request.ReportRequestDto;
import com.ssafy.relpl.dto.response.ReportListResponseDto;
import com.ssafy.relpl.dto.response.ReportResponseDto;
import com.ssafy.relpl.service.ReportService;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.CommonResult;

import com.ssafy.relpl.service.result.ListResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/report")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReportController {

    private final UserService userService;
    private final ReportService reportService;

    // @RequiredArgsConstructor 를 자동으로 생성해줘서 수동역할을 하는 @Autowired는 중복사용불가.
//    public ReportController(UserService userService, ReportService reportService) {
//        this.userService = userService;
//        this.reportService = reportService;
//    }

    // 제보 등록하기
    @PostMapping(value = "/regist")
    public ResponseEntity<CommonResult> registerReport(@RequestBody ReportRequestDto reportRequestDto) {
        try {
            // 제보 등록 요청 전달
            reportService.registerReport(reportRequestDto);

            // code와 message 받음 (CommonResult 사용)
            CommonResult response = new CommonResult();
            response.setCode(200);
            response.setMessage("제보 등록 성공");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            // 실패 시
            CommonResult response = new CommonResult();
            response.setCode(400);
            response.setMessage("제보 등록 실패");

            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
//    public ResponseEntity<ListResult<?>> registerReport(@RequestBody ReportListRequestDto reportListRequestDtoDto) {
////        log.info("제보 등록 요청 받음.");
////        ListResult<ReportListResponseDto> result = new ListResult<>();
////        result.setCode(200);
////
////        result.setMessage("제보 내역 조회 성공");
////
////        return ResponseEntity.ok(result);
//        List<ReportListResponseDto> reportList = reportService.reportLocation(20l);
//
//        ListResult<ReportListResponseDto> result = new ListResult<>();
//        result.setData(reportList);
//        result.setCode(200);
//        result.setMessage("제보 내역 조회 성공");
//
//        return ResponseEntity.ok(result);
//    }
//}
