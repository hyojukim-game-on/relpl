package com.ssafy.relpl.db.redis.repository;


import com.ssafy.relpl.dto.response.RankingEntry;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.stream.Collectors;



@Repository
public class RankingRepository {


    // 추후 redis 에서 sortedSet을 사용 예정이라서
    // 테스트용 더미데이터도 자바의 SortedSet 으로 구현
    SortedSet<RankingEntry> dailyRanking = new TreeSet<>(new Comparator<RankingEntry>() {
        @Override
        public int compare(RankingEntry r1, RankingEntry r2) {
            return Integer.compare(r2.getDistance(), r1.getDistance());
        }
    });


    // 테스트 할 때 RankingEntry 를 추가할 수 있는 메서드
    public void addRankingEntry(RankingEntry r) {
        dailyRanking.add(r);
    }


    /*
    * findDailyRanking :
    * @param :
    * @return :
    * */
    public List<RankingEntry> findDailyRanking() {
        // redis 에서 DailyRanking 1~20위 가져오기
        // test 용으로 Spring 에 저장된 데이터 dailyRanking 을 가져오기
        // test 할 때는 우선 requiredDate 이후인지 검증하는 로직을 제외
        // 추후 redis 연결되면 requiredDate 이후인지 검증하는 로직 추가하기
        return dailyRanking.stream()
                            .limit(20) // 상위 20개 요소 선택
                            .collect(Collectors.toList()); // List로 변환
    }


}