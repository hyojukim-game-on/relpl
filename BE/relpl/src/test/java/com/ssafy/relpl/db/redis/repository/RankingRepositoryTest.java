package com.ssafy.relpl.db.redis.repository;

import com.ssafy.relpl.dto.response.RankingEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RankingRepositoryTest {
    @Test
    public void testFindDailyRanking() {
        // 테스트 로직
        private RankingRepository rankingRepository;

        @BeforeEach
        public void setUp() {
            rankingRepository = new RankingRepository();
            rankingRepository.addRankingEntry(new RankingEntry("relpl01", 500));
            rankingRepository.addRankingEntry(new RankingEntry("relpl01", 500));
            rankingRepository.addRankingEntry(new RankingEntry("relpl02", 10));
            rankingRepository.addRankingEntry(new RankingEntry("relpl03", 400));
            rankingRepository.addRankingEntry(new RankingEntry("relpl04", 450));
            rankingRepository.addRankingEntry(new RankingEntry("relpl05", 800));
            rankingRepository.addRankingEntry(new RankingEntry("relpl06", 700));
            rankingRepository.addRankingEntry(new RankingEntry("relpl07", 600));
            rankingRepository.addRankingEntry(new RankingEntry("relpl08", 1000));
            rankingRepository.addRankingEntry(new RankingEntry("relpl09", 850));
            rankingRepository.addRankingEntry(new RankingEntry("relpl10", 300));
            rankingRepository.addRankingEntry(new RankingEntry("relpl11", 200));
            rankingRepository.addRankingEntry(new RankingEntry("relpl12", 110));
            rankingRepository.addRankingEntry(new RankingEntry("relpl13", 50));
            rankingRepository.addRankingEntry(new RankingEntry("relpl14", 111));
            rankingRepository.addRankingEntry(new RankingEntry("relpl16", 990));
            rankingRepository.addRankingEntry(new RankingEntry("relpl17", 432));
            rankingRepository.addRankingEntry(new RankingEntry("relpl18", 199));
            rankingRepository.addRankingEntry(new RankingEntry("relpl19", 873));
            rankingRepository.addRankingEntry(new RankingEntry("relpl20", 195));
            rankingRepository.addRankingEntry(new RankingEntry("relpl21", 349));
            rankingRepository.addRankingEntry(new RankingEntry("relpl22", 439));
            rankingRepository.addRankingEntry(new RankingEntry("relpl23", 169));
            rankingRepository.addRankingEntry(new RankingEntry("relpl24", 433));
        }

            // redis 대신 java의 SortedSet 이용하여 더미데이터 생성
            SortedSet<RankingEntry> dailyRanking = new TreeSet<>(new Comparator<RankingEntry>() {
                @Override
                public int compare(RankingEntry r1, RankingEntry r2) {
                    // 숫자(number)에 대해 내림차순 정렬
                    return Integer.compare(r2.getDistance(), r1.getDistance());
                }
            });


            // 초기화 블록
            {

            }



        }


        // 결과 검증
        assertNotNull(top20, "랭킹 리스트는 null이 아니어야 합니다.");
        assertTrue(top20.size() <= 20, "랭킹 리스트는 최대 20개의 요소를 포함해야 합니다.");

        // 필요한 경우, 반환된 리스트의 특정 내용을 검증하는 추가적인 assert문을 작성할 수 있습니다.

    }
}