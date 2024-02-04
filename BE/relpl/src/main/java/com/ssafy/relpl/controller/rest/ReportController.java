package com.ssafy.relpl.controller.rest;

import com.ssafy.relpl.dto.request.ReportListRequest;
import com.ssafy.relpl.dto.request.ReportRegistRequest;
import com.ssafy.relpl.dto.response.ReportListResponse;
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
    public ResponseEntity<CommonResult> registerReport(@RequestBody ReportRegistRequest reportRegistRequest) {
        try {
            log.info("여기는 컨트롤러다. 제보 등록 요청받음.");

            // 제보 등록 요청 전달
            CommonResult result = reportService.registerReport(reportRegistRequest);

            // 성공 시
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 실패 시
            log.error("제보 등록 중 오류 발생", e);
            CommonResult response = responseService.getFailResult(400, "제보 등록 실패");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // 제보 내역 조회하기
    @PostMapping(value = "/list")
    public ResponseEntity<ListResult<ReportListResponse>> getReportList(@RequestBody ReportListRequest reportListRequest) {
        try {
            log.info("여기는 컨트롤러다. 제보 내역 조회 요청받음.");

            // 제보 내역 조회 요청 전달
            ListResult<ReportListResponse> result = reportService.getReportList(reportListRequest.getUserId());

            // 성공 시
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            // 실패 시
            log.error("제보 내역 조회 중 오류 발생", e);
            ListResult<ReportListResponse> response = responseService.getFailResult(400, "제보 내역 조회 실패", Collections.emptyList());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
