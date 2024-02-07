package com.ssafy.relpl.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.relpl.db.postgre.entity.FcmToken;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.FcmTokenRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.FcmTokenRequest;
import com.ssafy.relpl.service.result.SingleResult;
import com.ssafy.relpl.util.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;
    private final ResponseService responseService;
    private final FirebaseMessaging firebaseMessaging;

    @Transactional
    public ResponseEntity<?> regist(FcmTokenRequest request) {
        log.info("FCMToken regist start");
        try {
            // userId에 해당하는 사용자가 이미 db에 FcmToken이 등록되어 있는지 확인하는 로직
            Optional<FcmToken> tokenOptional = fcmTokenRepository.findByUserId(request.getUserId());

            if (tokenOptional.isPresent()) {
                // FcmToken이 등록된 경우
                log.info("FCMToken update");
                FcmToken fcmToken = tokenOptional.get();

                fcmToken.setFcmToken(request.getFcmToken());
            } else {
                // FcmToken이 등록되지 않은 경우
                log.info("FCMToken create");
                FcmToken fcmToken = FcmToken.builder()
                        .userId(request.getUserId())
                        .fcmToken(request.getFcmToken())
                        .build();

                fcmTokenRepository.save(fcmToken);
            }

            // 성공 시 응답
            return ResponseEntity.ok(responseService.getSingleResult(true, "토큰 등록 성공", 200));
        } catch (Exception e) {
            // 실패 시 응답
            log.error("FCM Token 등록 중 오류 발생", e);
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "토큰 등록 실패"));
        }
    }

    public boolean sendMessage(FcmToken fcmToken) {
        Notification notification = Notification.builder()
                .setTitle("relpl title")
                .setBody("relpl body")
                .build();
        Message message = Message.builder()
                .setToken(fcmToken.getFcmToken())
                .setNotification(notification)
                .build();
        try {
            log.info("알림 전송 성공" );
            firebaseMessaging.send(message);
            return true;
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
            log.info("알림 전송 실패" );
            return false;
        }
    }

}
