package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.Coin;
import com.ssafy.relpl.db.postgre.entity.Report;
import com.ssafy.relpl.db.postgre.entity.RoadInfo;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.CoinRepository;
import com.ssafy.relpl.db.postgre.repository.ReportRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.ReportRegistRequest;
import com.ssafy.relpl.dto.response.ReportListResponse;
import com.ssafy.relpl.dto.response.TmapApiResponse;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.ListResult;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CoinRepository coinRepository;
    private final ResponseService responseService;
    private final RoadInfoService roadInfoService;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // 추가
    private final TmapService tmapService;

    // 더미 데이터 생성 메서드
    private Report createDummyReport(User user, ReportRegistRequest requestDto) {
        Report dummyReport = new Report();
        dummyReport.setUser(user);
        dummyReport.setReportDate(requestDto.getReportDate());

        org.springframework.data.geo.Point dummyPoint = requestDto.getReportCoordinate();
        dummyReport.setReportCoordinate(geometryFactory.createPoint(new Coordinate(dummyPoint.getX(), dummyPoint.getY())));

//        dummyReport.setTmapId(requestDto.getTmapId());

        return dummyReport;
    }

    // 제보 등록 메서드
    @Transactional
    public ResponseEntity<?> registerReport(ReportRegistRequest reportRegistRequest) {
        try {
            log.info("여기는 서비스단이다. 제보 등록 여부를 확인한다.");

            // userId 체크
            Long userId = reportRegistRequest.getUserId();
            if (userId == null) {
                // userId가 null인 경우 처리
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "유저 정보가 없습니다."));
            }

            // 등록되어 존재하는 유저인지 확인
            User existingUser = userRepository.findById(userId).orElse(null);
            if (existingUser != null) {
                // 등록되어 존재하는 유저인 경우
                Report report = createDummyReport(existingUser, reportRegistRequest);

                // TmapService를 통해 TmapApiResponse를 받아옴
                double longitude = report.getReportCoordinate().getX();
                double latitude = report.getReportCoordinate().getY();
                TmapApiResponse tmapApiResponse = tmapService.callTmapApi(longitude, latitude);
                if (tmapApiResponse != null && tmapApiResponse.getResultData() != null) {
                    TmapApiResponse.Header header = tmapApiResponse.getResultData().getHeader();
                    if (header != null) {
                        // TmapApiResponse의 linkId를 tmapId로 저장

                        report.setTmapId(header.getLinkId());
                        report = reportRepository.save(report);

                        Optional<RoadInfo> queryResult = roadInfoService.findByRoadHashId(header.getLinkId());
                        if (queryResult.isPresent()) {
                            RoadInfo roadInfo = queryResult.get();
                            roadInfo.setRoadInfoReport(roadInfo.getRoadInfoReport() + 1);
                            roadInfo.setRoadInfoTotalReport(roadInfo.getRoadInfoTotalReport() + 1);
//                            roadInfoService.updateRoadInfo(roadInfo);
                        } else {
                            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "제보 등록 실패, 근처 도로 존재하지 않음"));
                        }

                        // 보유 코인 조회
                        int sumCoin = coinRepository.sumCoinAmountByUserId(userId);
                        if (sumCoin >= 100) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            Coin coin = Coin.builder()
                                    .coinEventDate(simpleDateFormat.format(new Date()))
                                    .coinAmount(-100)
                                    .coinEventDetail("제보 등록")
                                    .user(report.getUser())
                                    .build();

                            coinRepository.save(coin);

                            return ResponseEntity.ok(responseService.getSingleResult(true, "제보 등록 성공", 200));
                        } else {
                            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "제보 등록 실패. 코인이 부족합니다."));
                        }
                    } else {
                        return ResponseEntity.badRequest().body(responseService.getFailResult(400, "제보 등록 실패. TmapApiResponse의 Header가 null입니다."));
                    }
                } else {
                    return ResponseEntity.badRequest().body(responseService.getFailResult(400, "제보 등록 실패. TmapApiResponse가 유효하지 않습니다."));
                }
            } else {
                // 등록되지 않은 유저인 경우
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "등록되지 않은 유저입니다."));
            }
        } catch (Exception e) {
            // 실패 시
            log.error("제보 등록 중 오류 발생", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "제보 등록 실패"));
        }
    }
    // 제보 내역 조회 메서드
    public ResponseEntity<?> getReportList(Long userId) {
        log.info("여기는 서비스단이다. 제보 내역을 조회한다.");

        try {
            // userId 체크
            if (userId == null) {
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "유저 정보가 없습니다.", Collections.emptyList()));
            }

            // 등록되어 존재하는 유저인지 확인
            Optional<User> existingUser = userRepository.findById(userId);

            if (existingUser.isPresent()) {
                // 등록되어 존재하는 유저인 경우 해당 유저의 제보 내역 조회
                List<Report> reportList = reportRepository.findByUser(existingUser.get());

                // 제보 내역이 존재하는지 확인.
                if (!reportList.isEmpty()) {
                    // 제보내역이 있으면 리스트 형태로 반환하기
                    List<ReportListResponse> responseList = new ArrayList<>();
                    for (Report report : reportList) {
                        ReportListResponse responseDto = new ReportListResponse(report);
                        responseList.add(responseDto);
                    }

                    return ResponseEntity.ok().body(responseService.getListResult(responseList, "제보내역 조회에 성공하였습니다."));
                } else {
                    // 제보내역이 없는 경우
                    return ResponseEntity.ok().body(responseService.getSingleResult(Collections.emptyList(), "제보내역 조회에 성공하였습니다. user의 제보 내역이 없습니다.", 200));
                }

            } else {
                // 등록되지 않은 유저인 경우
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "등록되지 않은 유저입니다.", Collections.emptyList()));
            }
        } catch (Exception e) {
            // 실패 시
            log.error("제보 내역 조회 중 오류 발생", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "제보 내역 조회 실패"));
        }

    }

    // 더미 테스트
//    public int getDummyTmapData() {
//        return 1301;
////        return new TmapApiResponse().getResultData().header.linkId;
//    }
}