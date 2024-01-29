package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.Coin;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.CoinRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.response.CoinScoreDataResponseDto;
import com.ssafy.relpl.dto.response.CoinScoreResponseDto;
import com.ssafy.relpl.service.ResponseService; // 왜 계속 회색으로 남아있냐??? ReportService도 그렇던데
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final ResponseService responseService;

    // 프로젝트 완료 시 지급하는 100 point coin
    private static final int PROJECT_COMPLETION_POINTS = 100;

    // 플로깅 프로젝트 100meter 주행 시, 10point coin 씩 지급
    private static final int PLOGGING_COMPLETION_POINTS = 10;

    // reportCoordinate의 위도,경도 위치를 한번씩 등록할때마다, 100point coin 씩 차감되는 것.
    private static final int LOCATION_FLAG_DEDUCTION_POINTS = 100;


    // 프로젝트 완료 시 지급하는 100 point coin 지급하는 로직 작성
    public void completeProject(Long userId) {
        log.info("프로젝트 완료 시 포인트 {}point 지급", PROJECT_COMPLETION_POINTS);
        updateUserCoin(userId, PROJECT_COMPLETION_POINTS);
    }



    // 플로깅 프로젝트 100meter 주행 시, 10point coin 씩 지급 로직
    public void completePlogging(Long userId) {
        log.info("100미터 플로깅 완료 시 포인트 {}point 지급", PLOGGING_COMPLETION_POINTS);
        updateUserCoin(userId, PLOGGING_COMPLETION_POINTS);
    }



    // reportCoordinate의 위도,경도 위치를 한번씩 등록할때마다, 100point coin 씩 차감되는  로직
    public void reportLocationFlag(Long userId) {
        log.info("위치 플래그 제보 시 포인트 {}point 차감", LOCATION_FLAG_DEDUCTION_POINTS);
        // 여기 포인트 차감 로직을 추가
        updateUserCoin(userId, -LOCATION_FLAG_DEDUCTION_POINTS);// 차감이라 음수?
    }

    // 유저의 현재 코인 정보 조회 및 업데이트 로직 (update vs select)
    private void updateUserCoin(Long userId, int coinAmount) {
        // 사용자의 현재 코인 정보 조회
        List<Coin> userCoins = coinRepository.findAllByUserId(userId);

        // 사용자의 현재 코인 총합 계산
        int userTotalCoin = calculateUserTotalCoin(userCoins);

        // 사용자의 현재 코인 정보 업데이트
        CoinScoreDataResponseDto coinScoreDataResponseDto = new CoinScoreDataResponseDto();
        coinScoreDataResponseDto.setUserTotalCoin(userTotalCoin);

        // eventList 구성
        List<CoinScoreResponseDto> eventList = new ArrayList<>();

        for (Coin coin : userCoins) {
            CoinScoreResponseDto coinScoreResponseDto = new CoinScoreResponseDto();
            coinScoreResponseDto.setCoinEventDate(coin.getCoinEventDate());
            coinScoreResponseDto.setCoinAmount(coin.getCoinAmount());
            coinScoreResponseDto.setCoinEventDetail(coin.getCoinEventDetail());
            eventList.add(coinScoreResponseDto);
        }

        coinScoreDataResponseDto.setEventList(eventList);

        // 사용자의 현재 코인 정보 업데이트 후 저장
        // 이 부분에서 적절한 로직으로 코인 정보를 업데이트하고 저장하는 부분을 구현해야 합니다.
         coinRepository.saveAll(userCoins);

        log.info("사용자 {}에게 {}포인트가 지급되었습니다. 현재 보유한 포인트: {}", userId, coinAmount, userTotalCoin);
    }


    // db 삽입 로직
    public void insertCoinData(Long userId, Long coinEventId, String coinEventDate, int coinAmount, String coinEventDetail) {
        log.info("포인트 데이터 삽입: userId={}, coinEventId={}, coinEventDate={}, coinAmount={}, coinEventDetail={}",
                userId, coinEventId, coinEventDate, coinAmount, coinEventDetail);

        Coin coin = Coin.builder()
                .userId(userId)
                .coinEventId(coinEventId)
                .coinEventDate(coinEventDate)
                .coinAmount(coinAmount)
                .coinEventDetail(coinEventDetail)
                .build();

        coinRepository.save(coin);

    }

    // userTotalCoin 만드는 로직
    public CoinScoreDataResponseDto getCoinScoreData(Long userId) {
        CoinScoreDataResponseDto coinScoreDataResponseDto = new CoinScoreDataResponseDto();

        // 사용자 모든 코인 내역 가져오기
        List<Coin> userCoins = coinRepository.findAllByUserId(userId);

        // userTotalCoin 계산
        int userTotalCoin = calculateUserTotalCoin(userCoins);
        coinScoreDataResponseDto.setUserTotalCoin(userTotalCoin);

        // eventList 구성
        List<CoinScoreResponseDto> eventList = new ArrayList<>();

        for (Coin coin : userCoins) {
            CoinScoreResponseDto coinScoreResponseDto = new CoinScoreResponseDto();
            coinScoreResponseDto.setCoinEventDate(coin.getCoinEventDate());
            coinScoreResponseDto.setCoinAmount(coin.getCoinAmount());
            coinScoreResponseDto.setCoinEventDetail(coin.getCoinEventDetail());
            eventList.add(coinScoreResponseDto);
        }

        coinScoreDataResponseDto.setEventList(eventList);

        return coinScoreDataResponseDto;
    }
    private int calculateUserTotalCoin(List<Coin> userCoins) {
        int userTotalCoin = 0;

        for (Coin coin : userCoins) {
            userTotalCoin += coin.getCoinAmount();
        }

        return userTotalCoin;
    }
}
