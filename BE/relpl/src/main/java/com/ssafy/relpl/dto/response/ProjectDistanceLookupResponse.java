package com.ssafy.relpl.dto.response;

import com.ssafy.relpl.db.postgre.entity.Project;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.geo.Point;

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


    public void addUserMoveMemo(String userMoveMemo) {
        if (this.userMoveMemo == null) {
            this.userMoveMemo = userMoveMemo;
        } else {
            // 이미 값이 존재할 경우 새로운 값 추가 또는 처리하는 로직을 여기에 작성
            // 예를 들어, 기존 값과 새로운 값의 조합, 덧붙이기 등을 수행할 수 있습니다.
            this.userMoveMemo = this.userMoveMemo + " / " + userMoveMemo;
        }
    }

    public void addUserMoveImage(String userMoveImage) {
        if (this.userMoveImage == null) {
            this.userMoveImage = userMoveImage;
        } else {
            // 이미 값이 존재할 경우 새로운 값 추가 또는 처리하는 로직을 여기에 작성
            // 예를 들어, 기존 값과 새로운 값의 조합, 덧붙이기 등을 수행할 수 있습니다.
            this.userMoveImage = this.userMoveImage + " / " + userMoveImage;
        }
    }

}
