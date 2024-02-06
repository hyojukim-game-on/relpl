package com.ssafy.relpl.util.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.locationtech.jts.geom.Point;

import java.util.List;

@AllArgsConstructor
@Builder
public class UserHistoryDetailEntry {

    String userNickname;
//    List<Point> movePath;
    String moveStart;
    String moveEnd;
    int moveDistance;
    String moveTime;
    String moveMemo;
    int moveContribution;
    String moveImage;

}
