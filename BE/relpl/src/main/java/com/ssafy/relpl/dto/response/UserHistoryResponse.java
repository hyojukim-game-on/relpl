package com.ssafy.relpl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserHistoryResponse {
    int totalProject;
    int userTotalDistance;
    int userTotalTime;
    ArrayList<Map<String, Object>> detailList; // 각 릴레이의 정보가 담긴 리스트
}
