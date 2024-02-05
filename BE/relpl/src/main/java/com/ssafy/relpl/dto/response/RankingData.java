package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.util.common.RankingEntry;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RankingData {
    private List<RankingEntry> dailyRanking;
    private List<RankingEntry> weeklyRanking;
    private List<RankingEntry> monthlyRanking;
}
