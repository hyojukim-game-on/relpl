package com.ssafy.relpl.service;


import com.ssafy.relpl.db.postgre.entity.Report;
import com.ssafy.relpl.db.postgre.repository.ReportRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.ReportRequestDto;
import com.ssafy.relpl.dto.response.ReportListResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

//    @Autowired
//    //제보 등록- 사용자가 마커를 표시한 위치(위,경도)를 서버로 넘긴다.
//    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
//        this.reportRepository = reportRepository;
//        this.userRepository = userRepository;
//    }

    public void registerReport(ReportRequestDto reportRequestDto) {
        // 제보 등록 로직 예시 db에 저장
        Report report = new Report();
        report.setId(reportRequestDto.getUserId());
        report.setReportDate("2024-01-25");
        report.setReportCoordinate(new Point(35.12f, 126.213f));
//        report.setReportCoordinate(reportRequestDto.getReportCoordinate());
        reportRepository.save(report);
    }




    // 이건 제보 내역 조회
    public List<ReportListResponseDto> reportLocation(Long id) {
        log.info("제보 내역을 조회한다.");
        // 여기에 로직을 추가

//        List<currentUserReport> report = reportrepository.get()
        List<ReportListResponseDto> reportList = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            reportList.add(ReportListResponseDto.builder()

                    .reportCoordinate(new Point(35.12f, 126.213f))
                    .reportDate("2024-01-24")
                    .build());
        }

        return reportList;
    }

}


