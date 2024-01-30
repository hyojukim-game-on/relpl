//package com.ssafy.relpl.db.redis.repository;
//
//import com.ssafy.relpl.db.redis.entity.DailyRanking;
//import com.ssafy.relpl.db.redis.entity.MonthlyRanking;
//import com.ssafy.relpl.db.redis.entity.WeeklyRanking;
//import com.ssafy.relpl.service.result.CommonResult;
//import org.springframework.data.repository.CrudRepository;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//public abstract class RankingRepository implements CrudRepository {
//    public abstract ResponseEntity<CommonResult> findByDailyEndTime(String rankingTime);
//    public abstract ResponseEntity<CommonResult> findByWeeklyEndTime(String rankingTime);
//    public abstract ResponseEntity<CommonResult> findByMonthlyEndTime(String rankingTime);
//
//    public abstract List<DailyRanking> sample_findByDailyEndTime();
//}
