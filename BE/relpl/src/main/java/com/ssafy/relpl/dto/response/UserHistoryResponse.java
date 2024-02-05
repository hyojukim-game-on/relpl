package com.ssafy.relpl.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public class UserHistoryResponse {
    int totalProject;
    int userTotalDistance;
    int userTotalTime;
    List<?> detailList;
}
