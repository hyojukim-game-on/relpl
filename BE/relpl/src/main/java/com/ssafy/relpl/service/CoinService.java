package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.Coin;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.CoinRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.response.CoinScoreDataResponseDto;
import com.ssafy.relpl.dto.response.CoinScoreResponseDto;
import com.ssafy.relpl.service.ResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
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
    private static final int PLOGGING_SHORT_POINTS = 10;

    // reportCoordinate의 위도,경도 위치를 한번씩 등록할때마다, 100point coin 씩 차감되는 것.
    private static final int LOCATION_FLAG_DEDUCTION_POINTS = -100;


    // 프로젝트 완료 시 지급하는 100 point coin 지급하는 로직 작성
    public void completeProject(Long userId) {
        log.info("프로젝트 완료 시 포인트 {}point 지급", PROJECT_COMPLETION_POINTS);
        updateUserCoin(userId, PROJECT_COMPLETION_POINTS, generateCoinEventDetail());
    }

    // 플로깅 프로젝트 100meter 주행 시, 10point coin 씩 지급 로직
    public void completeShortPlogging(Long userId) {
        log.info("100미터 플로깅 완료 시 포인트 {}point 지급", PLOGGING_SHORT_POINTS);
        updateUserCoin(userId, PLOGGING_SHORT_POINTS, generateCoinEventDetail());
    }

    // reportCoordinate의 위도,경도 위치를 한번씩 등록할때마다, 100point coin 씩 차감되는  로직
    public void reportLocationFlag(Long userId) {
        log.info("위치 플래그 제보 시 포인트 {}point 차감", LOCATION_FLAG_DEDUCTION_POINTS);
        // 여기 포인트 차감 로직을 추가
        updateUserCoin(userId, LOCATION_FLAG_DEDUCTION_POINTS, generateCoinEventDetail());
    }

    public Long generateCoinEventId() {
        // 여기에 동적으로 CoinEventId를 생성하는 로직을 작성하세요.
        // 예를 들어, 현재 시간을 기반으로 한 값을 사용할 수 있습니다.
        return System.currentTimeMillis();
    }

    public String generateCoinEventDate() {
        // 여기에 동적으로 CoinEventDate를 생성하는 로직을 작성하세요.
        // 예를 들어, 현재 시간을 기반으로 한 값을 사용할 수 있습니다.
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public int generateCoinAmount() {
        // 여기에 동적으로 CoinAmount를 생성하는 로직을 작성하세요.
        // 예를 들어, 랜덤 값을 사용할 수 있습니다.
        return new Random().nextInt(100) + 1; // 1부터 100 사이의 랜덤 값
    }

    public String generateCoinEventDetail() {
        // 여기에 동적으로 CoinEventDetail을 생성하는 로직을 작성하세요.
        // 예를 들어, 랜덤 문자열을 사용할 수 있습니다.
        int length = 10;
        return RandomStringUtils.randomAlphanumeric(length);
    }

    // 유저의 현재 코인 정보 조회 및 업데이트 로직 (update vs select)
    private void updateUserCoin(Long userId, int coinAmount, String coinEventDetail) {
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
