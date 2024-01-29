package com.ssafy.relpl.db.redis.repository;


import com.ssafy.relpl.db.redis.entity.DailyRanking;
import com.ssafy.relpl.db.redis.entity.MonthlyRanking;
import com.ssafy.relpl.db.redis.entity.WeeklyRanking;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class RankingRepositoryImpl<T, ID> implements RankingRepository<T, ID> {


    // 24.01.29 12:30PM codeReview


    // 초기 데이터 넣을 때 (20개 반환이 minimum 인데 ?)
    // 만약에 20개 미만이면 있는 거만 보여줄 건지 아니면 20개 될때까지 부족하다고 할 건지
    public List<DailyRanking> createDailyRanking(LocalDate requiredDate) {}

    public List<WeeklyRanking> createWeeklyRanking(LocalDate requiredDate) {}

    public List<MonthlyRanking> createMonthlyRanking(LocalDate requiredDate) {}

    // 랭킹 데이터 업데이트 할 때 (집계 관련)
    public List<DailyRanking> updateDailyRanking(LocalDate requiredDate) {}

    public List<WeeklyRanking> updateWeeklyRanking(LocalDate requiredDate) {}

    public List<MonthlyRanking> updateMonthlyRanking(LocalDate requiredDate) {}

    // 랭킹 데이터 조회 할 때 (유저에게 표출 관련)
    public List<DailyRanking> getDailyRanking(LocalDate requiredDate) {}

    public List<WeeklyRanking> getWeeklyRanking(LocalDate requiredDate) {}

    public List<MonthlyRanking> getMonthlyRanking(LocalDate requiredDate) {}

    // 랭킹 데이터 삭제 할 때 (이 때 dailyEndTime 사용)
    public List<DailyRanking> deleteDailyRanking(LocalDate requiredDate) {}

    // 랭킹 데이터 삭제 할 때 (이 때 weeklyEndTime 사용)
    public List<WeeklyRanking> deleteWeeklyRanking(LocalDate requiredDate) {}

    // 랭킹 데이터 삭제 할 때 (이 때 monthlyEndTime 사용)
    public List<MonthlyRanking> deleteMonthlyRanking(LocalDate requiredDate) {}


    @Override
    public Object save(Object entity) {
        return null;
    }

    @Override
    public Iterable saveAll(Iterable entities) {
        return null;
    }

    @Override
    public Optional findById(Object o) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Object o) {
        return false;
    }

    @Override
    public Iterable findAll() {
        return null;
    }

    @Override
    public Iterable findAllById(Iterable iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Object o) {

    }

    @Override
    public void delete(Object entity) {

    }

    @Override
    public void deleteAllById(Iterable iterable) {

    }

    @Override
    public void deleteAll(Iterable entities) {

    }

    @Override
    public void deleteAll() {

    }
}