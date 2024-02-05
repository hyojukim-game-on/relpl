package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Project;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectCreateDistanceResponse {
    long projectId;

    public static ProjectCreateDistanceResponse createProjectCreateDistanceResponse(Project project) {
        return ProjectCreateDistanceResponse.builder()
                .projectId(project.getProjectId())
                .build();
    }
}
