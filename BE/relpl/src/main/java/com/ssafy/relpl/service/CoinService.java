package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.Coin;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.CoinRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.response.CoinBarcodeResponse;
import com.ssafy.relpl.dto.response.CoinScoreDataResponse;
import com.ssafy.relpl.dto.response.CoinScoreResponse;
import com.ssafy.relpl.service.result.SingleResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {

    private final CoinRepository coinRepository;
    private final UserRepository userRepository;
    private final ResponseService responseService;


    // 코인 지급 내역 조회 메서드
    public ResponseEntity<?> coinScore(Long userId) {
        log.info("여기는 서비스단이다. 코인 지급 내역을 조회한다.");

        // userId 체크
        if (userId == null) {
            // userId가 null인 경우 처리
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "유저 정보가 없습니다.", new CoinScoreDataResponse()));
        }

        // 등록되어 존재하는 유저인지 확인
        User existingUser = userRepository.findById(userId).orElse(null);

        if (existingUser != null) {
            // 등록되어 존재하는 유저인 경우
            // 코인 지급 내역 조회
            List<Coin> coins = coinRepository.findAllByUserUserId(userId);

            CoinScoreDataResponse response = new CoinScoreDataResponse();
            // 총 코인 액수 계산
            int totalCoin = 0;

            List<CoinScoreResponse> coinScoreResponses = new ArrayList<>();

            for (Coin coin : coins) {
                totalCoin += coin.getCoinAmount();

                CoinScoreResponse coinScoreResponse = CoinScoreResponse.convertFromCoin(coin);
                coinScoreResponses.add(coinScoreResponse);
            }

            response.setUserTotalCoin(totalCoin);
            response.setEventList(coinScoreResponses);

            return ResponseEntity.ok(responseService.getSingleResult(response, "코인 지급 내역 조회 성공", 200));
        } else {
            // 등록되지 않은 유저인 경우
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "등록되지 않은 유저입니다.", null));
        }
    }


    // coinBarcode 조회 메서드
    public ResponseEntity<?> coinBarcode(Long userId) {
        log.info("여기는 서비스단이다. 코인 바코드를 조회한다.");

        // userId 체크
        if (userId == null) {
            // userId가 null인 경우 처리
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "유저 정보가 없습니다.", new CoinBarcodeResponse()));
        }

        // 등록되어 존재하는 유저인지 확인
        User existingUser = userRepository.findById(userId).orElse(null);

        if (existingUser != null) {
            // 등록되어 존재하는 유저인 경우
            // 코인 지급 내역 조회
            List<Coin> coins = coinRepository.findAllByUserUserId(userId);

            // 총 코인 액수 계산
            int totalCoin = 0;
            for (Coin coin : coins) {
                totalCoin += coin.getCoinAmount();
            }

            // 응답 데이터 구성
            CoinBarcodeResponse responseDto = new CoinBarcodeResponse();
            responseDto.setUserTotalCoin(totalCoin);

            return ResponseEntity.ok(responseService.getSingleResult(responseDto, "바코드 조회 성공", 200));
        } else {
            // 등록되지 않은 유저인 경우
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "등록되지 않은 유저입니다.", false));
        }
    }
}