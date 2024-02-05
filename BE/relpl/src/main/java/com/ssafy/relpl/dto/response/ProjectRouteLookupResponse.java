package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Project;
import lombok.Builder;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;

@Data
@Builder
public class ProjectRouteLookupResponse {

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
    private GeoJsonLineString recommendLineString;

    public static ProjectRouteLookupResponse buildFromProject(Project project) {
        return ProjectRouteLookupResponse.builder()
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
                .recommendLineString(project.getRecommendLineString()) // 추가된 부분
                .build();
    }
}
