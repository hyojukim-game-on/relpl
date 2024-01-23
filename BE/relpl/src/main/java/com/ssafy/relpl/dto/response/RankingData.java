package com.ssafy.relpl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;



@Getter
@Setter
public class RankingData {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class RankingEntry {
        private String nickname;
        private int distance;
    }

    private List<RankingEntry> dailyRanking;
    private List<RankingEntry> weeklyRanking;
    private List<RankingEntry> monthlyRanking;


}
