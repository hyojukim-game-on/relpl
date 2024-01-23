package com.ssafy.relpl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoinService {

    private static final int PROJECT_COMPLETION_POINTS = 100;
    private static final int PLOGGING_COMPLETION_POINTS = 10;

    // 프로젝트 완료 시 포인트 지급
    public void completeProject() {
        log.info("프로젝트 완료 시 포인트 {}point 지급", PROJECT_COMPLETION_POINTS);
        // 여기 포인트 지급 로직을 추가
    }

    // 플로깅 완료 시 포인트 지급
    public void completePlogging() {
        log.info("100미터 플로깅 완료 시 포인트 {}point 지급", PLOGGING_COMPLETION_POINTS);
        // 여기 포인트 지급 로직을 추가
    }
}
