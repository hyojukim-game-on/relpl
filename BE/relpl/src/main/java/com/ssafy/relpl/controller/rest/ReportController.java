package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.ReportListRequestDto;
import com.ssafy.relpl.dto.request.ReportRegistRequestDto;
import com.ssafy.relpl.dto.response.ReportListResponseDto;
import com.ssafy.relpl.service.ReportService;
import com.ssafy.relpl.service.ResponseService;
import com.ssafy.relpl.service.UserService;
import com.ssafy.relpl.service.result.CommonResult;

import com.ssafy.relpl.service.result.ListResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/api/report")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ReportController {

    private final UserService userService;
    private final ReportService reportService;
    private final ResponseService responseService;

    // 제보 등록하기
    @PostMapping(value = "/regist")
    public ResponseEntity<CommonResult> registerReport(@RequestBody ReportRegistRequestDto reportRegistRequestDto) {
        try {
            log.info("여기는 컨트롤러다. 제보 등록 요청받음.");
            // 제보 등록 요청 전달
            CommonResult result = reportService.registerReport(reportRegistRequestDto);

            // 성공시
//            CommonResult result = responseService.getSingleResult(true, "제보 등록 성공입니둥", 200);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 실패 시
            CommonResult response = responseService.getFailResult(400, "제보 등록 실패");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // 제보 내역 조회하기
    @PostMapping(value = "/list")
    public ResponseEntity<ListResult<ReportListResponseDto>> getReportList(@RequestBody ReportListRequestDto reportListRequestDto) {

        try {
            log.info("여기는 컨트롤러다. 제보 내역 조회 요청받음.");
            ListResult<ReportListResponseDto> reportList = reportService.reportLocation(reportListRequestDto.getUserId());
            // 성공시
            ListResult<ReportListResponseDto> result = new ListResult<>();
            List<ReportListResponseDto> data = reportList.getData();
            result.setData(data);
            result.setCode(200);
            result.setMessage("제보 내역 조회 성공입니둥");

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            //실패시
            log.error("제보 내역 조회 중 오류 발생", e);
            ListResult<ReportListResponseDto> failListResult = responseService.getFailResult(400, "제보 내역 조회 실패", Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(failListResult);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
