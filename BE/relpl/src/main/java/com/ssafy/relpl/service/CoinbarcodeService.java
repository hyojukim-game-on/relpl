package com.ssafy.relpl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoinbarcodeService {

    private static final int LOCATION_FLAG_DEDUCTION_POINTS = 100;

    // 위치 플래그 제보 시 포인트 차감
    public void reportLocationFlag() {
        log.info("위치 플래그 제보 시 포인트 {}point 차감", LOCATION_FLAG_DEDUCTION_POINTS);
        // 여기 포인트 차감 로직을 추가
    }
}
