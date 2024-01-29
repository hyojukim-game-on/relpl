package com.ssafy.relpl.service;


import com.ssafy.relpl.db.redis.entity.DailyRanking;
import com.ssafy.relpl.db.redis.entity.MonthlyRanking;
import com.ssafy.relpl.db.redis.entity.WeeklyRanking;
import com.ssafy.relpl.db.redis.repository.RankingRepository;
import com.ssafy.relpl.dto.response.RankingDataDto;
import com.ssafy.relpl.dto.response.RankingEntry;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ResponseService responseService;
    private final RankingRepository rankingRepository;


    /*
    * getRanking : Redis DB 에서 일간, 주간, 월간 랭킹을 조회하여 반환해주는 함수
    * @param : String rankingTime (Path Variable 로 AD 로부터 받음)
    * @return : 성공 or 실패 각각에 따른 응답 (code, msg, data 반환)
    * */
    public SingleResult<?> getRanking(String rankingTime) {

        // rankingTime 타입 변환 (String -> LocalDate)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate requiredDate = LocalDate.parse(rankingTime, formatter);

        // rankingTime 유효성 검사
        // 실패 시 로직 1. rankingTime 누락되었을 경우
        try{
            if (rankingTime.isEmpty()) {
                return responseService.getFailResult( 400, "rankingTime 필드가 누락되었습니다.");
            }
                // 실패 시 로직 2. rankingTime 이 미래의 날짜일 경우
            else if (requiredDate.isAfter(LocalDate.now())) {
                return responseService.getFailResult(400, "실패");
            }
                // 성공 시 로직
            else {
                // rankingTime 에 따라 다른 랭킹 데이터를 가져오기

                List<DailyRanking> dailyRanking = rankingRepository.getDailyRanking(requiredDate);
                List<WeeklyRanking> weeklyRanking = rankingRepository.getWeeklyRanking(requiredDate);
                List<MonthlyRanking> monthlyRanking = rankingRepository.getMonthlyRanking(requiredDate);

                // rankingRepository 에서 꺼낸 데이터는 DailyRanking Entity 형태인데
                // DailyRanking Entity -> List<RankingEntry> 는 RankingService 에서 수행



                // RankingDataDto 객체 (API response 의 data key 에 할당될 내용) 생성
                RankingDataDto rankingData = RankingDataDto.builder()
                        .dailyRanking(dailyRanking)
                        .weeklyRanking(weeklyRanking)
                        .monthlyRanking(monthlyRanking)
                        .build();

                // 성공했을 때의 결과 반환
                return responseService.getSingleResult(rankingData, "랭킹 조회에 성공하였습니다.", 200);
            }
        } // 실패 시 로직 3. "yyyy-MM-dd" 형식이 아닐 경우
        catch (DateTimeParseException e) {
            return responseService.getFailResult(400, "yyyy-MM-dd 형식이 아닙니다.");
        }


    }

}