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
    private Point projectStopCoordinate;

    private int progress;
    private String userMoveMemo;
    private String userMoveImage;

    public static ProjectDistanceLookupResponse buildFromProject(Project project) {
        return ProjectDistanceLookupResponse.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .projectTotalContributer(project.getProjectTotalContributer())
                .projectTotalDistance(project.getProjectTotalDistance())
                .projectRemainingDistance(project.getProjectRemainingDistance())
                .projectCreateDate(project.getProjectCreateDate())
                .projectEndDate(project.getProjectEndDate())
                .projectIsPath(project.isProjectIsPath())
                .projectStopCoordinate(project.getProjectStopCoordinate())
                .userMoveMemo(project.getUserMoveMemo()) // 수정된 부분
                .userMoveImage(project.getUserMoveImage()) // 수정된 부분
                .build();
    }

}
