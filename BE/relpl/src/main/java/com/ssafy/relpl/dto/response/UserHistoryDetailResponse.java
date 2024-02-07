package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.util.common.UserHistoryDetailEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;



@Getter
@AllArgsConstructor
@Builder
public class UserHistoryDetailResponse {
    String projectName;
    int projectDistance;
    int projectTime;
    int projectPeople;
    List<UserHistoryDetailEntry> detailList;
}
