package com.ssafy.relpl.dto.response;

import lombok.*;

import java.util.Map;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FcmDataMessage {
    private boolean validate_only;
    private Message message;

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private Notification notification;
        private String token;
        private Map<String, String> data;

        public Message(Notification notification, String token) {
            super();
            this.notification = notification;
            this.token = token;
        }
    }

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
}