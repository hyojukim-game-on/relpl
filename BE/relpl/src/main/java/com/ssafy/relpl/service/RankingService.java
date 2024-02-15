package com.ssafy.relpl.service;


import com.ssafy.relpl.dto.response.RankingData;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.SingleResult;
import com.ssafy.relpl.util.common.RankingEntry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
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

    /* 유저가 플로깅 중단할 때마다 addOrUpdateRanking 호출
    * addOrUpdateRanking : 기존에 있는 유저이면 랭킹 누적거리 더해주기, 없으면 랭킹에 새로 올리기
    * @param : 유저 닉네임, 해당 유저가 이번에 이동한 거리
    * @return : 200 OK or 400 Bad Request
    * */
    public ResponseEntity<CommonResult> addOrUpdateRanking(String nickname, double moveDistance) {
        log.info("서비스 내부로 들어옴");
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
            return ResponseEntity.status(HttpStatus.OK).body(responseService.getSingleResult(true, "랭킹 업데이트 성공", 200));
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getSingleResult(true, "랭킹 업데이트 실패", 400));
        }
    }

    /* checkMemberExists : [REDIS] 일간/주간/월간랭킹 키 값에 유저 닉네임 있는지 READ
     * @param : 일간/주간/월간랭킹, 닉네임
     * @return : Redis 의 sorted set 의 일간/주간/월간랭킹 key 로 조회해서
     *           해당 유저 nickname 이 있으면 그동안의 누적 거리 반환
     *           해당 유저 nickname 이 없으면 null 반환
     * */
    public Optional<Double> checkMemberExists(String key, String nickname) {
        return Optional.ofNullable(zSetOperations.score(key, nickname));
    }


    // redis 에 rankingKey key , nickname member , moveDistance score 로 추가된 값 insert

    public void addOrUpdateMember(String key, String nickname, double moveDistance) {
        Boolean isAdded = zSetOperations.add(key, nickname, moveDistance);
        log.info("addOrUpdate_isAdded:{}",isAdded);
    }

    /* updateRankingFor : 유저 랭킹 업데이트 실행
     * @param : 일간/주간/월간랭킹, 닉네임, 유저가 이번에 움직인 거리
     * @return : 없음
     * */
    public void updateRankingFor(String key, String nickname, double moveDistance) {
        Optional<Double> presentMoveDistance = checkMemberExists(key, nickname);

        // 기존에 있던 유저일 경우 누적 거리 늘려서 REDIS 에 UPDATE 하는 함수 호출
        if (presentMoveDistance.isPresent()) {
            double newTotalDistance = presentMoveDistance.get() + moveDistance;
            addOrUpdateMember(key, nickname, newTotalDistance);
        } else { // 기존에 없던 유저일 경우 REDIS 에 UPDATE 하는 함수 호출
            addOrUpdateMember(key, nickname, moveDistance);
        }
    }

    /* getNowRanking : 요청받았을 때의 Redis 에 있는 데일리랭킹 1~20위 반환
    * @parameter : 없음
    * @return : 200 OK / 400 랭킹 조회 중 오류 발생
    * */
    public ResponseEntity<CommonResult> getNowRanking() {
        try {

            // [REDIS] 일간랭킹 키에 저장된 [유저 닉네임, 플로깅 누적 거리] 1~20위 반환
            // ORDER BY 플로깅 누적 거리 내림차순
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
            assert dailyRankingRedis != null;
            for (ZSetOperations.TypedTuple<String> person : dailyRankingRedis) {
                if (!person.getValue().isEmpty()) { // 닉네임이 빈 문자열이 아닌 경우만 리스트에 추가
                    dailyRankingList.add(new RankingEntry(person.getValue(), person.getScore()));
                }
            }

            assert weeklyRankingRedis != null;
            for (ZSetOperations.TypedTuple<String> person : weeklyRankingRedis) {
                if (!person.getValue().isEmpty()) { // 닉네임이 빈 문자열이 아닌 경우만 리스트에 추가
                    weeklyRankingList.add(new RankingEntry(person.getValue(), person.getScore()));
                }
            }

            assert monthlyRankingRedis != null;
            for (ZSetOperations.TypedTuple<String> person : monthlyRankingRedis) {
                if (!person.getValue().isEmpty()) { // 닉네임이 빈 문자열이 아닌 경우만 리스트에 추가
                    monthlyRankingList.add(new RankingEntry(person.getValue(), person.getScore()));
                }
            }

            // 빌더 패턴을 사용하여 RankingDataDto 객체를 생성
            RankingData rankingData = RankingData.builder()
                    .dailyRanking(dailyRankingList)
                    .weeklyRanking(weeklyRankingList)
                    .monthlyRanking(monthlyRankingList)
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(responseService.getSingleResult(rankingData, "랭킹 조회 성공했습니다.", 200));

        } catch (RedisConnectionFailureException e) {
            log.error("Redis 연결 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "REDIS 연결 실패"));
        } catch (Exception e) {
            log.error("랭킹 조회 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseService.getFailResult(400, "랭킹 조회 중 오류 발생"));
        }
    }
    
    // 갱신 주기마다 TTL 이 24시/7일/한달로 설정된 daily/weekly/monthlyRanking 이 redis 에 추가되도록 하기
    public void resetRankingTest() {
        log.info("resetRankingTest");
        // 테스트 용으로 Redis 에 testRanking 을 넣어주기
        Boolean isAdded = zSetOperations.add("testRanking","테스트",15);
        log.info("daily_isAdded:{}",isAdded);
        // testRanking 키가 5초 후에 만료되도록 하기
        redisTemplate.expire("testRanking", 5, TimeUnit.SECONDS);
    }

    // 매일 자정에 dailyRanking (TTL(유효기간)=24시간) 으로 다시 만들어주기
    @Scheduled(cron = "0 0 0 * * *")
    public void resetDailyRankingTTL() {
        // Redis 에 dailyRanking 을 넣어주기
        Boolean isAdded = zSetOperations.add("dailyRanking","",0);
        log.info("daily_isAdded:{}",isAdded);
        // dailyRanking 키가 24시간 후에 만료되도록 하기
        redisTemplate.expire("dailyRanking", 24, TimeUnit.HOURS);
    }

    // 매일 일요일 자정에 weeklyRanking (TTL(유효기간)=7일) 으로 다시 만들어주기
    @Scheduled(cron = "0 0 0 * * SUN")
    public void resetWeeklyRankingTTL() {
        // Redis 에 weeklyRanking 을 넣어주기
        Boolean isAdded = zSetOperations.add("weeklyRanking","",0);
        log.info("weekly_isAdded:{}",isAdded);
        // weeklyRanking 키가 7일 후에 만료되도록 하기
        redisTemplate.expire("weeklyRanking", 168, TimeUnit.HOURS);
    }

    // 매월 1일 자정에 Monthly Ranking의 TTL 설정
    @Scheduled(cron = "0 0 0 1 * *")
    public void resetMonthlyRankingTTL() {
        // TTL 설정 로직
        // 매 달 1일부터 매 달 말일까지 남은 기간을 시간 단위 ttlInHours 로 계산
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        long daysUntilEndOfMonth = today.until(lastDayOfMonth).getDays() + 1;
        long ttlInHours = daysUntilEndOfMonth * 24;
        // Redis 에 monthlyRanking 을 넣어주기
        Boolean isAdded = zSetOperations.add("monthlyRanking","",0);
        log.info("monthly_isAdded:{}",isAdded);
        // monthlyRanking 키가 ttlInHours 의 시간 단위 후에 expire 되도록 하기
        redisTemplate.expire("monthlyRanking", ttlInHours, TimeUnit.HOURS);
    }

}