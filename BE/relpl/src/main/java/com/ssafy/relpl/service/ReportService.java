package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.Report;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.ReportRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.ReportListRequestDto;
import com.ssafy.relpl.dto.request.ReportRegistRequestDto;
import com.ssafy.relpl.dto.response.ReportListResponseDto;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.ListResult;
import com.ssafy.relpl.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.data.geo.Point;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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

    // 더미 데이터 생성 메서드
    private Report createDummyReport(User user, ReportRegistRequestDto requestDto) {
        Report dummyReport = new Report();
        dummyReport.setUser(user);
        dummyReport.setReportDate(requestDto.getReportDate());

        Point dummyPoint = requestDto.getReportCoordinate();
        dummyReport.setReportCoordinate(geometryFactory.createPoint(new Coordinate(dummyPoint.getX(), dummyPoint.getY())));

        dummyReport.setTmapId(requestDto.getTmapId());

        return dummyReport;
    }

    // 제보 등록 메서드
    public CommonResult registerReport(ReportRegistRequestDto reportRegistRequestDto) {
        log.info("여기는 서비스단이다. 제보 등록 여부를 확인한다.");

        // userId 체크
        Long userId = reportRegistRequestDto.getUserId();
        if (userId == null) {
            // userId가 null인 경우 처리
            return responseService.getFailResult(400, "유저 정보가 없습니다.");
        }

        // 등록되어 존재하는 유저인지 확인
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser != null) {
            // 등록되어 존재하는 유저인 경우
            Report dummyReport = createDummyReport(existingUser, reportRegistRequestDto);
            dummyReport = reportRepository.save(dummyReport);

            if (dummyReport.getUser() != null) {
                return responseService.getSingleResult(true, "등록된 유저입니다. 제보 등록 성공", 200);
            } else {
                return responseService.getFailResult(400, "제보 등록 실패");
            }
        } else {
            // 등록되지 않은 유저인 경우
            return responseService.getFailResult(400, "등록되지 않은 유저입니다.");
        }
    }

    // 제보 내역 조회 메서드
    public ListResult<ReportListResponseDto> getReportList(Long userId) {
        log.info("여기는 서비스단이다. 제보 내역을 조회한다.");

        // userId 체크
        if (userId == null) {
            return responseService.getFailResult(400, "유저 정보가 없습니다.", Collections.emptyList());
        }

        // 등록되어 존재하는 유저인지 확인
        User existingUser = userRepository.findById(userId).orElse(null);

        if (existingUser != null) {
            // 등록되어 존재하는 유저인 경우 해당 유저의 제보 내역 조회
            List<Report> reportList = reportRepository.findByUser(existingUser);

            // 제보 내역이 존재하는지 확인.
            if (!reportList.isEmpty()) {
                // 제보내역이 있으면 리스트 형태로 반환하기
                List<ReportListResponseDto> responseList = new ArrayList<>();
                for (Report report : reportList) {
                    ReportListResponseDto responseDto = new ReportListResponseDto(report);
                    responseList.add(responseDto);
                }

                return responseService.getListResult(responseList, "제보내역 조회에 성공하였습니다.");
            } else {
                // 제보내역이 없는 경우
                return responseService.getFailResult(400, "제보내역이 없는 유저입니다.", Collections.emptyList());
            }

        } else {
            // 등록되지 않은 유저인 경우
            return responseService.getFailResult(400, "등록되지 않은 유저입니당.", Collections.emptyList());
        }
    }
}