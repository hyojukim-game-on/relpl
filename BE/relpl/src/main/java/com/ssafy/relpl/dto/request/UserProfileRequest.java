package com.ssafy.relpl.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserProfileRequest {
    private Long userId;
    private MultipartFile file;
}
