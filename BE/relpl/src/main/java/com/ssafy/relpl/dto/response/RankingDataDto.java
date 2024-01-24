package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.redis.entity.DailyRanking;
import com.ssafy.relpl.db.redis.entity.MonthlyRanking;
import com.ssafy.relpl.db.redis.entity.WeeklyRanking;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
@Builder
public class RankingDataDto {
    private List<DailyRanking> dailyRanking;
    private List<WeeklyRanking> weeklyRanking;
    private List<MonthlyRanking> monthlyRanking;
}
