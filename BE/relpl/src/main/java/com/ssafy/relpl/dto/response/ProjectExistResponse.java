package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Project;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

@Builder
@Data
public class ProjectExistResponse {
    boolean isExist;
    Long projectId;
    Point startPoint;

    public static ProjectExistResponse createProjectExistDto(Project project) {
        return ProjectExistResponse.builder()
                .projectId(project.getProjectId())
                .isExist(true)
                .startPoint(new Point(project.getProjectStopCoordinate().getX(), project.getProjectStopCoordinate().getY()))
                .build();
    }

    public static ProjectExistResponse createProjectNotExistDto() {
        return ProjectExistResponse.builder()
                .projectId(-1L)
                .isExist(false)
                .startPoint(new Point(-1, -1))
                .build();
    }
}
