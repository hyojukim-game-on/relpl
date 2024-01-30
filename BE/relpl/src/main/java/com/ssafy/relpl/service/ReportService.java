package com.ssafy.relpl.service;


import com.ssafy.relpl.db.postgre.entity.Report;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.ReportRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.ReportRegistRequestDto;
import com.ssafy.relpl.dto.response.ReportListResponseDto;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.ListResult;
import com.ssafy.relpl.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ResponseService responseService;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // 제보 등록하기 로직
    public CommonResult registerReport(ReportRegistRequestDto reportRegistRequestDto) {
        log.info("여기는 서비스단이다 . 제보 등록 여부를 확인한다.");

        // userId 체크
        Long userId = reportRegistRequestDto.getUserId();
        if (userId == null) {
            // userId가 null인 경우 처리
            return responseService.getFailResult(400, "유저 정보가 없습니다.");
        }


        // 제보 등록 로직 예시 db에 저장
        Report report = new Report();
        //기존부분
//        report.setUserId(reportRegistRequestDto.getUserId());

        // 수정부분
        report.setUser(userRepository.findById(String.valueOf(reportRegistRequestDto.getUserId())).orElse(null));


        report.setReportDate(reportRegistRequestDto.getReportDate());
        Point start = reportRegistRequestDto.getReportCoordinate();
        Coordinate coordinate = new Coordinate(start.getX(), start.getY());
        report.setReportCoordinate(geometryFactory.createPoint(coordinate));

    // ReportRegistRequestDto에서 tmapId 직접 받아오기
        Long tmapId = reportRegistRequestDto.getTmapId();

        // tmapId가 null이면 기본값인 0L로 설정
        report.setTmapId(tmapId != null ? tmapId : 0L);


//        report.setReportCoordinate(reportRegistRequestDto.getReportCoordinate());
//        report.setReportId(null);

        report = reportRepository.save(report);
//        reportRepository.flush();//삽입 호출

        // report.getUserId() 값이 null인 경우에 실패로 간주하고 처리
//        if (report.getUserId() != null) {
//            return responseService.getSingleResult(true, "제보 등록 성공", 200);
//        } else {
//            return responseService.getFailResult(400, "제보 등록 실패");
//        }

        if (report.getUser() != null) {
            return responseService.getSingleResult(true, "제보 등록 성공", 200);
        } else {
            return responseService.getFailResult(400, "제보 등록 실패");
        }


    }




    // 제보 내역 조회 로직
    public ListResult<ReportListResponseDto> reportLocation(Long userId) {
        log.info("여기는 서비스단이다 . 제보 내역을 조회한다.");
        // 여기 로직을 추가
        try {
            List<ReportListResponseDto> reportList = new ArrayList<>();
            for (long i = 0; i < 10; i++) {
                reportList.add(ReportListResponseDto.builder()
                        .reportCoordinate(new Point(35.12f, 126.213f))
                        .reportDate("2022-01-24")
                        .build());
            }

            return responseService.getListResult(reportList, "제보 내역 조회 성공");
        } catch (Exception e) {
            log.error("제보내역 조회 중 오류 발생", e);
            return responseService.getFailResult(400,"제보 내역 조회 실패",  Collections.emptyList());
        }
    }
}


