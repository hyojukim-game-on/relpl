package com.ssafy.relpl.db.postgre.entity;

import com.ssafy.relpl.dto.request.ProjectCreateDistanceRequest;
import com.ssafy.relpl.dto.request.ProjectCreateRouteRequest;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "project")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "project_create_date", nullable = true)
    private String projectCreateDate;

    @Column(name = "project_end_date", nullable = true)
    private String projectEndDate;

    @Column(name = "project_total_distance")
    private int projectTotalDistance;

    @Column(name = "project_start_coordinate")
    private Point projectStartCoordinate;

    @Column(name = "project_end_coordinate")
    private Point projectEndCoordinate;

    @Column(name = "project_stop_coordinate")
    private Point projectStopCoordinate;

    @Column(name = "project_ispath")
    private boolean projectIsPath;

    @Column(name = "project_isdone")
    // default = false
    private boolean projectIsDone;

    @Column(name = "project_isplogging")
    // default = true
    private boolean projectIsPlogging;

    @Column(name = "project_remaining_distance")
    private int projectRemainingDistance;

    @Column (name = "project_total_contributer")
    private int projectTotalContributer;

    @Column(name = "project_coordinate_total_size")
    private int projectCoordinateTotalSize;

    @Column(name = "project_coordinate_current_index")
    private int projectCoordinateCurrentSize;

    public static Project createDistanceProject(ProjectCreateDistanceRequest request, Point startPoint) {
        return Project.builder()
                .userId(request.getUserId())
                .projectName(request.getProjectName())
                .projectCreateDate(request.getProjectCreateDate())
                .projectEndDate(request.getProjectEndDate())
                .projectStartCoordinate(startPoint)
                .projectStopCoordinate(startPoint)
                .projectEndCoordinate(null)
                .projectIsPath(false)
                .projectRemainingDistance(request.getProjectTotalDistance())
                .projectTotalDistance(request.getProjectTotalDistance())
                .projectIsDone(false)
                .projectIsPlogging(false)
                .projectTotalContributer(0)
                .build();
    }

    public static Project createRouteProject(ProjectCreateRouteRequest request, Point startPoint, Point endPoint) {
        return Project.builder()
                .userId(request.getUserId())
                .projectName(request.getProjectName())
                .projectCreateDate(request.getProjectCreateDate())
                .projectEndDate(request.getProjectEndDate())
                .projectStartCoordinate(startPoint)
                .projectStopCoordinate(startPoint)
                .projectEndCoordinate(endPoint)
                .projectIsPath(true)
                .projectRemainingDistance(request.getProjectSelectedTotalDistance())
                .projectTotalDistance(request.getProjectSelectedTotalDistance())
                .projectCoordinateTotalSize(request.getProjectSelectedCoordinateTotalSize())
                .projectIsDone(false)
                .projectIsPlogging(false)
                .projectTotalContributer(0)
                .build();

    }

    // progress 진행률 계산
    public int calculateProgress() {
        if (projectCoordinateTotalSize == 0) {
            // 예외 처리: 분모가 0일 경우, progress는 0으로 설정
            return 0;
        }

        double progressPercentage = ((double) projectCoordinateCurrentSize / projectCoordinateTotalSize) * 100;
        return (int) progressPercentage;
    }

}
