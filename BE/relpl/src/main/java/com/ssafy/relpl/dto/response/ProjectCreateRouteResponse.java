package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Project;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectCreateRouteResponse {
    long projectId;

    public static ProjectCreateRouteResponse createProjectCreateRouteResponse(Project project) {
        return ProjectCreateRouteResponse.builder()
                .projectId(project.getProjectId())
                .build();
    }
}
