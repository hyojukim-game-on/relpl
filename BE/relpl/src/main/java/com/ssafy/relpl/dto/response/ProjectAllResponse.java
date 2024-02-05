package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Project;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

@Data
@Builder
public class ProjectAllResponse {
    Long projectId;
    Point stopCoordinate;
    boolean path;

    public static ProjectAllResponse createProjectAllResponse(Project project) {
        return ProjectAllResponse.builder()
                .projectId(project.getProjectId())
                .stopCoordinate(new Point (project.getProjectStopCoordinate().getX(), project.getProjectStopCoordinate().getY()))
                .path(project.isProjectIsPath())
                .build();
    }
}
