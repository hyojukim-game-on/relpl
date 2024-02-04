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

    private final ReportService reportService;

    // 제보 등록하기
    @PostMapping(value = "/regist")
    public ResponseEntity<?> registerReport(@RequestBody ReportRegistRequest reportRegistRequest) {
        log.info("여기는 컨트롤러다. 제보 등록 요청받음.");
        // 제보 등록 요청 전달
        return reportService.registerReport(reportRegistRequest);
    }

    // 제보 내역 조회하기
    @PostMapping(value = "/list")
    public ResponseEntity<?> getReportList(@RequestBody ReportListRequest reportListRequest) {
        log.info("여기는 컨트롤러다. 제보 내역 조회 요청받음.");
        // 제보 내역 조회 요청 전달
        return reportService.getReportList(reportListRequest.getUserId());
    }
}
