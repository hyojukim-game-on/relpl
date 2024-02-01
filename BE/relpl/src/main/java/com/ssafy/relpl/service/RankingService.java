package com.ssafy.relpl.service;



import com.ssafy.relpl.dto.response.RankingDataDto;
import com.ssafy.relpl.dto.response.RankingEntry;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.SingleResult;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RankingService {


    private final RedisTemplate<String, String> redisTemplate; // RedisTemplate 주입
    private final ResponseService responseService;
    private ZSetOperations<String, String> zSetOperations; // Sorted Set 을 다루기 위한 인터페이스


    @PostConstruct
    private void init() {
        zSetOperations = redisTemplate.opsForZSet(); 
        log.info("zSetOperations 초기화 완료");
        // zSetOperations 초기화
    }



    public CommonResult addOrUpDateRanking(String nickname, double moveDistance) {

        String dailyRanking = "dailyRanking";
        String weeklyRanking = "weeklyRanking";
        String monthlyRanking = "monthlyRanking";

        
        try {
            updateRankingFor(dailyRanking, nickname, moveDistance);
            log.info("dailyRanking 업데이트 완료");
//        resetRankingTest();
//        log.info("5초 후 redis 에서 dailyRanking 키가 사라집니다 !");

            updateRankingFor(weeklyRanking, nickname, moveDistance);
            log.info("weeklyRanking 업데이트 완료");

            updateRankingFor(monthlyRanking, nickname, moveDistance);
            log.info("monthlyRanking 업데이트 완료");

            // 결과 반환
            return responseService.getSuccessResult("랭킹 업데이트 성공");
        } catch (Exception e){
            log.error(e.getMessage());
            return responseService.getFailResult(400, "랭킹 업데이트 실패");
        }
    }


    // redis 에 nickname 으로 moveDistance 있는지 검사
    public Optional<Double> checkMemberExists(String key, String nickname) {
        return Optional.ofNullable(zSetOperations.score(key, nickname));
    }


    // redis 에 rankingKey key , nickname member , moveDistance score 로 추가된 값 insert
    public void addOrUpdateMember(String key, String nickname, double moveDistance) {
        Boolean isAdded = zSetOperations.add(key, nickname, moveDistance);
    }


    // redis 에 rankingKey 에 대한 moveDistance 값이 있으면 추가, 없으면 새로 넣어주기
    public void updateRankingFor(String key, String nickname, double moveDistance) {
        Optional<Double> presentMoveDistance = checkMemberExists(key, nickname);

        if (presentMoveDistance.isPresent()) {
            double newTotalDistance = presentMoveDistance.get() + moveDistance;
            addOrUpdateMember(key, nickname, newTotalDistance);
        } else {
            addOrUpdateMember(key, nickname, moveDistance);
        }
    }


    /* getNowRanking : 요청받았을 때의 Redis 에 있는 데일리랭킹 1~20위 반환
    * @parameter : 없음
    * @return : SingleResult<RankingDataDto>
    {
    "code": 200,
    "msg": "랭킹 조회 성공했습니다.",
    "data": {
            "dailyRanking": [{"nickname": "aaa", "distance": 800.0},{},{}...{} ],
            "weeklyRanking": [{"nickname": "aaa", "distance": 800.0},{},{}...{} ],
            "monthlyRanking": [{"nickname": "aaa", "distance": 800.0},{},{}...{} ]
            }
    }
    * */
    public SingleResult<RankingDataDto> getNowRanking() {
        try {
            // LinkedHashSet 형태로 redis 에서 key = dailyRanking 에 해당하는 값들 반환 받기
            Set<ZSetOperations.TypedTuple<String>> dailyRankingRedis = zSetOperations.reverseRangeWithScores("dailyRanking", 0, 20);
            log.info("dailyRankingRedis:{}", dailyRankingRedis);

            Set<ZSetOperations.TypedTuple<String>> weeklyRankingRedis = zSetOperations.reverseRangeWithScores("weeklyRanking", 0, 20);
            log.info("weeklyRankingRedis:{}", weeklyRankingRedis);

            Set<ZSetOperations.TypedTuple<String>> monthlyRankingRedis = zSetOperations.reverseRangeWithScores("monthlyRanking", 0, 20);
            log.info("monthlyRankingRedis:{}", monthlyRankingRedis);

            // RankingDataDto 응답 객체 초기화
            List<RankingEntry> dailyRankingList = new ArrayList<>();
            List<RankingEntry> weeklyRankingList = new ArrayList<>();
            List<RankingEntry> monthlyRankingList = new ArrayList<>();

            // List<RankingEntry> 형태로 LinkedHashSet 변환 (순회하면서 할당해주기)
            // Value = nickname , Score = moveDistance (유저 플로깅 누적 거리)
            for (ZSetOperations.TypedTuple<String> person : dailyRankingRedis) {
                dailyRankingList.add(new RankingEntry(person.getValue(), person.getScore()));
            }

            for (ZSetOperations.TypedTuple<String> person : weeklyRankingRedis) {
                weeklyRankingList.add(new RankingEntry(person.getValue(), person.getScore()));
            }

            for (ZSetOperations.TypedTuple<String> person : monthlyRankingRedis) {
                monthlyRankingList.add(new RankingEntry(person.getValue(), person.getScore()));
            }


            // 빌더 패턴을 사용하여 RankingDataDto 객체를 생성
            RankingDataDto rankingDataDto = RankingDataDto.builder()
                    .dailyRanking(dailyRankingList)
                    .weeklyRanking(weeklyRankingList)
                    .monthlyRanking(monthlyRankingList)
                    .build();

            return responseService.getSingleResult(rankingDataDto, "랭킹 조회 성공했습니다.", 200);
        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패: {}", e.getMessage());
            return (SingleResult<RankingDataDto>) responseService.getFailResult(400, "Redis 연결 오류 발생");
        } catch (Exception e) {
            log.error("랭킹 조회 중 오류 발생: {}", e.getMessage());
            return (SingleResult<RankingDataDto>) responseService.getFailResult(400, "랭킹 조회 중 오류 발생");
            }
    }
    
    // TimeToLive (TTL) - 랭킹 갱신 주기마다 기존 키 값 만료되도록 하기
    public void resetRankingTest() {
        // TTL 설정 로직
        // 5초뒤에 dailyRanking 키에 할당된 것들이 사라짐
        redisTemplate.expire("dailyRanking", 5, TimeUnit.SECONDS);
    }




    // 매일 자정에 Daily Ranking 의 TTL 설정
    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyRankingTTL() {
        // TTL 설정 로직
        redisTemplate.expire("dailyRanking", 24, TimeUnit.HOURS);
    }

    // 매주 일요일 자정에 Weekly Ranking의 TTL 설정
    @Scheduled(cron = "0 0 0 * * SUN")
    public void resetWeeklyRankingTTL() {
        // TTL 설정 로직
        redisTemplate.expire("weeklyRanking", 168, TimeUnit.HOURS);
    }

    // 매월 1일 자정에 Monthly Ranking의 TTL 설정
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyRankingTTL() {
        // TTL 설정 로직
        // 일단 28일 후에 만료되도록 설정해둠 (하드코딩)
        redisTemplate.expire("monthlyRanking", 672, TimeUnit.HOURS);
    }
    
    
    
    
    
}