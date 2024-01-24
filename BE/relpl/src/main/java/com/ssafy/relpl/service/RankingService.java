package com.ssafy.relpl.service;


import com.ssafy.relpl.db.redis.repository.RankingRepository;
import com.ssafy.relpl.dto.response.RankingDataDto;
import com.ssafy.relpl.dto.response.RankingEntry;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService {

    // repository와 다른 필요한 컴포넌트를 주입받음
    private final RankingRepository rankingRepository;


    public SingleResult<RankingDataDto> getRanking(String rankingTime) {

        // 예시: rankingTime에 따라 다른 랭킹 데이터를 조회
        List<RankingEntry> dailyRanking = rankingRepository.find(rankingTime);
        List<RankingEntry> weeklyRanking = rankingRepository.findWeeklyRanking(rankingTime);
        List<RankingEntry> monthlyRanking = rankingRepository.findMonthlyRanking(rankingTime);

        // RankingDataDto 객체 생성
        RankingDataDto rankingData = RankingDataDto.builder()
                                            .dailyRanking(dailyRanking)
                                            .weeklyRanking(weeklyRanking)
                                            .monthlyRanking(monthlyRanking)
                                            .build();

        // DTO 생성
        RankingDataDto rankingDataDto = new RankingDataDto(rankingData, true, "랭킹 데이터 조회 성공");
        // 결과 반환
        return new SingleResult<>(rankingDataDto, true, "랭킹 조회 성공");
    }

}