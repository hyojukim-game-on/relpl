package com.ssafy.relpl.service;

import com.ssafy.relpl.db.postgre.entity.FcmToken;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.FcmTokenRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.FcmTokenRequest;
import com.ssafy.relpl.service.result.SingleResult;
import com.ssafy.relpl.util.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final ResponseService responseService;



    public ResponseEntity<?> checkFcmToken(FcmTokenRequest request) throws BaseException {
        // 존재하는 userId인지 확인하는 로직
        Long userId = request.getUserId();
        if (userRepository.existsById(userId)) {
            //존재하는 userId인 경우
            return insertOrUpdateFcmToken(userId, request.getFcmToken());
        } else {
            // 존재하지않은 userId인 경우
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "회원가입되지 않은 유저입니다."));
        }
    }

    private ResponseEntity<?> insertOrUpdateFcmToken(Long userId, String fcmToken) {
        try {
            // userId에 해당하는 사용자가 이미 db에 FcmToken이 등록되어 있는지 확인하는 로직
            FcmToken existingToken = fcmTokenRepository.findByUserUserId(userId);

            if (existingToken != null) {
                // 이미 FcmToken이 등록된 경우, Fcmtoken을 update한다.
                existingToken.setFcmToken(fcmToken);
            } else {
                // 등록된 FcmToken이 없는 경우, Fcmtoken을 insert한다.

                //userRepository.findById(userId)를 사용하여 사용자를 찾는 이유는 FcmToken을 생성할 때 해당 사용자를 참조해야하기 때문
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BaseException("유저를 찾을 수 없습니다."));

                existingToken = FcmToken.builder()
                        .fcmToken(fcmToken)
                        .user(user)
                        .build();
            }
            fcmTokenRepository.save(existingToken);

            // 성공 시 응답
            return ResponseEntity.ok(responseService.getSingleResult(true, "토큰 등록 성공", 200));
        } catch (Exception e) {
            log.error("FCM Token 등록 중 오류 발생", e);
            // 실패 시 응답
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "토큰 등록 실패"));
        }
    }
}
