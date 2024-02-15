package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProjectStopPhotoRequest {
    private Long userId;
    private Long projectId;
    private MultipartFile moveImage;
}
