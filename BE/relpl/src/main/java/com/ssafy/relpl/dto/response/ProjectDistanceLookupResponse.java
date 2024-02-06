package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Project;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.geom.Point;

@Data
@Builder
public class ProjectDistanceLookupResponse {

    private Long projectId;
    private String projectName;
    private int projectTotalContributer;
    private int projectTotalDistance;
    private int projectRemainingDistance;
    private String projectCreateDate;
    private String projectEndDate;
    private boolean projectIsPath;
    private Point projectStopCoordinate; // 해당 projectId에 UserRoute에서 가장 마지막 늦은 시간에 기록된 userMoveEnd에 해당하는 projectStopCoordinate 좌표

    private int progress; // 해당 projectId의 진행률 % (% 단위는 생략)
    private String userMoveMemo; //해당 projectId의 마지막에 기록된 userId의 userMoveMemo
    private String userMoveImage; // 해당 projectId의 마지막에 기록된 userId의 userMoveImage



}
