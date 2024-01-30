package com.ssafy.relpl.service;


import com.ssafy.relpl.config.RedisConfig;
import com.ssafy.relpl.db.redis.entity.DailyRanking;
import com.ssafy.relpl.db.redis.repository.RankingRepository;
import com.ssafy.relpl.dto.response.RankingDataDto;
import com.ssafy.relpl.dto.response.RankingEntry;
import com.ssafy.relpl.service.result.CommonResult;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@Service
@Builder
@RequiredArgsConstructor
public class RankingService {

    private final ResponseService responseService;
    private final RankingRepository rankingRepository;
    private final RedisConfig redisConfig;


    /*
    * getDailyRanking : Redis 에 저장된 일간 랭킹을 조회해주는 함수
    * @param : String rankingTime 랭킹을 요청하는 시점
    * @return : 저장된 일간 랭킹을 반환
    * */
    public ResponseEntity<CommonResult> getDailyRanking(String rankingTime) {
        return rankingRepository.findByDailyEndTime(rankingTime);
    }

    public List<DailyRanking> sample_getDailyRanking() {
        return rankingRepository.sample_findByDailyEndTime();
    }

    public List<RankingEntry> getRankingList() {
        String key = "dailyRanking";
        ZSetOperations<?, ?> stringStringZSetOperations = redisConfig.redisTemplate().opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringStringZSetOperations.reverseRangeWithScores(key, 0, 10);
        List<RankingEntry> collect = typedTuples.stream().map(ResponseRankingDto::convertToResponseRankingDto).collect(Collectors.toList());
        return collect;

    }



    /*
     * getWeeklyRanking : Redis 에 저장된 주간 랭킹을 조회해주는 함수
     * @param : String rankingTime 랭킹을 요청하는 시점
     * @return : 저장된 주간 랭킹을 반환
     * */
//    public SingleResult<ResponseEntity<CommonResult>> getWeeklyRanking(String rankingTime) {
//        ResponseEntity<CommonResult> weeklyRankings = rankingRepository.findByDailyEndTime(rankingTime);
//        return responseService.getSingleResult(weeklyRankings);
//    }


    /*
     * getMonthlyRanking : Redis 에 저장된 월간 랭킹을 조회해주는 함수
     * @param : String rankingTime 랭킹을 요청하는 시점
     * @return : 저장된 월간 랭킹을 반환
     * */
//    public SingleResult<ResponseEntity<CommonResult>> getMonthlyRanking(String rankingTime) {
//        ResponseEntity<CommonResult> monthlyRankings = rankingRepository.findByDailyEndTime(rankingTime);
//        return responseService.getSingleResult(monthlyRankings);
//    }



    /*
    * getRanking : 유효성 검사 후 일간/주간/월간 랭킹을 DTO 형태로 반환 해주는 함수
    * @param : String rankingTime 랭킹을 요청하는 시점
    * @return : 성공 or 실패 각각에 따른 응답 (code, msg, data 반환)
    * */
    public ResponseEntity<CommonResult> getRanking(String rankingTime) {

        // rankingTime 타입 변환 (String -> LocalDate)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate requiredDate = LocalDate.parse(rankingTime, formatter);

        // rankingTime 유효성 검사
        try{
            if (rankingTime.isEmpty()) { // 실패 시 로직 1. rankingTime 누락되었을 경우
                return ResponseEntity.badRequest().body(responseService.getFailResult( 400, "rankingTime 필드가 누락되었습니다."));
            }
            else if (requiredDate.isAfter(LocalDate.now())) { // 실패 시 로직 2. rankingTime 이 미래의 날짜일 경우
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "미래의 날짜에 대한 랭킹을 요청하였습니다."));
            } else {

            }
        }
        catch (DateTimeParseException e) {  // 실패 시 로직 3. "yyyy-MM-dd" 형식이 아닐 경우
                return ResponseEntity.badRequest().body(responseService.getFailResult(400, "yyyy-MM-dd 형식이 아닙니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(responseService.getFailResult(500, "서버 오류입니다."));
        }
    }



}