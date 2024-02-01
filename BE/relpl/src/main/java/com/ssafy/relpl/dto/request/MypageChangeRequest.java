package com.ssafy.relpl.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class MypageChangeRequest {
    private Long userId;
    private String userNickname;
    private String userPhone;
    private MultipartFile userProfilePhoto;
}
