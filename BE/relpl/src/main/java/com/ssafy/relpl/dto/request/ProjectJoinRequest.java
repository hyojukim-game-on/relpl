package com.ssafy.relpl.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectJoinRequest {
    private Long userId;
    private Long projectId;
}
