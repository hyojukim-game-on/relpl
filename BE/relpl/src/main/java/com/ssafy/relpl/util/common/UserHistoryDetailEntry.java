package com.ssafy.relpl.util.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.geo.Point;

import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class UserHistoryDetailEntry {

    String userNickname;
    List<Point> movePath;
    String moveStart;
    String moveEnd;
    int moveDistance;
    String moveTime;
    String moveMemo;
    int moveContribution;
    String moveImage;

}
