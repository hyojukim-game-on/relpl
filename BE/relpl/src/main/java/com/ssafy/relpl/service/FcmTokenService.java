package com.ssafy.relpl.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.relpl.db.postgre.entity.FcmToken;
import com.ssafy.relpl.db.postgre.entity.User;
import com.ssafy.relpl.db.postgre.repository.FcmTokenRepository;
import com.ssafy.relpl.db.postgre.repository.UserRepository;
import com.ssafy.relpl.dto.request.FcmTokenRequest;
import com.ssafy.relpl.dto.response.FcmDataMessage;
import com.ssafy.relpl.service.result.SingleResult;
import com.ssafy.relpl.util.exception.BaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.antlr.v4.runtime.Token;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {


    private final String API_URL = "https://fcm.googleapis.com/v1/projects/relpl-1b1f9/messages:send";
    private final FcmTokenRepository fcmTokenRepository;
    private final ResponseService responseService;
    private final ObjectMapper objectMapper;
//    private final FirebaseMessaging firebaseMessaging;

    private List<String> clientTokens = new ArrayList<>();

    // 클라이언트 토큰 관리
    public void clearTokens() {
        clientTokens.clear();
    }
    public void addTokens(String token) {
        clientTokens.add(token);
    }

    @Transactional
    public ResponseEntity<?> insert(FcmTokenRequest request) {
        log.info("FCM Token 등록");

        try {
            // 해당 토큰으로 등록된 레코드 삭제
            Optional<FcmToken> tokenOptional = fcmTokenRepository.findByFcmToken(request.getFcmToken());
            if(tokenOptional.isPresent()) {
                fcmTokenRepository.delete(tokenOptional.get());
            }
            // userId에 해당하는 사용자가 이미 db에 FcmToken이 등록되어 있는지 확인하는 로직
            Optional<FcmToken> fcmTokenOptional = fcmTokenRepository.findByUserId(request.getUserId());

            // 이미 FcmToken이 등록된 경우
            if (fcmTokenOptional.isPresent()) {
                FcmToken fcmToken = fcmTokenOptional.get();

                fcmToken.setFcmToken(request.getFcmToken());    //fcmToken update
            } else {
                // 등록된 FcmToken이 없는 경우
                FcmToken fcmToken = FcmToken.builder()
                        .fcmToken(request.getFcmToken())
                        .userId(request.getUserId())
                        .build();

                fcmTokenRepository.save(fcmToken);  //fcmToken save
            }
            log.info("FCM Token 등록 성공");

            // 성공 시 응답
            return ResponseEntity.ok(responseService.getSingleResult(true, "토큰 등록 성공", 200));
        } catch (Exception e) {
            log.error("FCM Token 등록 중 오류 발생", e);

            // 실패 시 응답
            return ResponseEntity.badRequest().body(responseService.getFailResult(400, "토큰 등록 실패"));
        }
    }

    // 등록된 모든 토큰을 이용해서 broadcasting
    public int broadCastDataMessage(String title, String body) throws IOException {
        for(String token: clientTokens) {
            log.debug("broadcastmessage : {},{},{}",token, title, body);
            sendDataMessageTo(token, title, body);
        }
        return clientTokens.size();
    }

    //targetToken에 해당하는 device로 FCM 푸시 알림 전송
    public void sendDataMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeDataMessage(targetToken, title, body);
        log.info("message : {}", message);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), message);
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                // 전송 토큰 추가
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.body().string());
        log.info("message : {}", message);
    }

    //FCM 알림 메시지 생성
    private String makeDataMessage(String targetToken, String title, String body) throws JsonProcessingException {
//        Notification noti = new FcmMessage.Notification(title, body, null);
        Map<String,String> map = new HashMap<>();
        map.put("title", title);
        map.put("body", body);

        FcmDataMessage.Message message = new FcmDataMessage.Message();
        message.setToken(targetToken);
        message.setData(map);

        FcmDataMessage fcmMessage = new FcmDataMessage(false, message);

        return objectMapper.writeValueAsString(fcmMessage);
    }

    //FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/firebase_service_key.json";

        // GoogleApi를 사용하기 위해 oAuth2를 이용해 인증한 대상을 나타내는객체
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())         // 서버로부터 받은 service key 파일 활용
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform")); // 인증하는 서버에서 필요로 하는 권한 지정

        googleCredentials.refreshIfExpired();
        String token = googleCredentials.getAccessToken().getTokenValue();

        return token;
    }


//    public boolean sendMessage(FcmToken fcmToken) {
//        Notification notification = Notification.builder()
//                .setTitle("relpl title")
//                .setBody("relpl body")
//                .build();
//        Message message = Message.builder()
//                .setToken(fcmToken.getFcmToken())
//                .setNotification(notification)
//                .build();
//        try {
//            log.info("알림 전송 성공" );
//            firebaseMessaging.send(message);
//            return true;
//        } catch (FirebaseMessagingException e) {
//            e.printStackTrace();
//            log.info("알림 전송 실패" );
//            return false;
//        }
//    }

}
