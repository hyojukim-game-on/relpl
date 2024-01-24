package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {

//    CoinRepository coinRepository;

    private final UserRepository userRepository;

    private static final int PROJECT_COMPLETION_POINTS = 100;
    private static final int PLOGGING_COMPLETION_POINTS = 10;

    private static final int LOCATION_FLAG_DEDUCTION_POINTS = 100;


//    public Coin savecoin() {
//        Coin coin = new Coin();
//        coin.setName("코인");
//        coin.setPrice(1000);
//        coin.setCount(0);
//        coin.setUserId(this.userRepository.save(new User()).getId());
//        return coin;
//    }

    // 프로젝트 완료 시 포인트 지급
    public void completeProject() {
        log.info("프로젝트 완료 시 포인트 {}point 지급", PROJECT_COMPLETION_POINTS);

        // 여기에 포인트 지급 로직을 추가
//        updateUserCoin(PROJECT_COMPLETION_POINTS);
    }

//    private void updateUserCoin(int points) {
//        // 사용자의 현재 코인 정보를 조회하는 로직
//        User user = userRepository.findById(userId).orElse(null);
//
//        if (user != null) {
//            // 현재 보유한 코인 수량 가져오기
//            int currentCoinCount = user.getCoinCount();
//
//            // 보유한 코인 수량 업데이트
//            user.setCoinCount(currentCoinCount + points);
//
//            // 사용자 정보 저장
//            userRepository.save(user);
//
//            log.info("사용자 {}에게 {}포인트가 지급되었습니다. 현재 보유한 포인트: {}",
//                    user.getId(), points, user.getCoinCount());
//        } else {
//            log.error("사용자를 찾을 수 없습니다.");
//            // 예외 처리 또는 오류 핸들링을 추가할 수 있습니다.
//        }
//    }


    // 플로깅 완료 시 포인트 지급
    public void completePlogging() {
        log.info("100미터 플로깅 완료 시 포인트 {}point 지급", PLOGGING_COMPLETION_POINTS);

        // 여기에 포인트 지급 로직을 추가
//        updateUserCoin(PLOGGING_COMPLETION_POINTS);
    }

//    private void updateUserCoin(int points) {
//        // 사용자의 현재 코인 정보를 조회하는 로직
//        User user = userRepository.findById(userId).orElse(null);
//
//        if (user != null) {
//            // 현재 보유한 코인 수량 가져오기
//            int currentCoinCount = user.getCoinCount();
//
//            // 보유한 코인 수량 업데이트
//            user.setCoinCount(currentCoinCount + points);
//
//            // 사용자 정보 저장
//            userRepository.save(user);
//
//            log.info("사용자 {}에게 {}포인트가 지급되었습니다. 현재 보유한 포인트: {}",
//                    user.getId(), points, user.getCoinCount());
//        } else {
//            log.error("사용자를 찾을 수 없습니다.");
//            // 예외 처리 또는 오류 핸들링을 추가할 수 있습니다.
//        }
//    }




    // 위치 플래그 제보 시 포인트 차감
    public void reportLocationFlag() {
        log.info("위치 플래그 제보 시 포인트 {}point 차감", LOCATION_FLAG_DEDUCTION_POINTS);
        // 여기 포인트 차감 로직을 추가
    }
}
