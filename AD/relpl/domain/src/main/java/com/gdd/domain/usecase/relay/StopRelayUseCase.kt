package com.gdd.domain.usecase.relay

import com.gdd.domain.model.Point
import com.gdd.domain.repository.ProjectRepository
import java.io.File
import javax.inject.Inject

class StopRelayUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(
        userId: Long,
        projectId: Int,
        userNickname: String,
        projectName: String,
        moveStart: String,
        moveEnd: String,
        moveDistance: Int,
        moveTime: Int,
        movePath: List<Point>,
        moveMemo: String,
        projectCoordinateCurrentSize: Int,
        photoFile: File,
    ): Result<Boolean> {
        val stopResult = projectRepository.stopProject(
            userId,
            projectId,
            userNickname,
            projectName,
            moveStart,
            moveEnd,
            moveDistance,
            moveTime,
            movePath,
            moveMemo,
            projectCoordinateCurrentSize
        )
        val picResult = projectRepository.stopProjectPic(photoFile,userId,projectId)

        return if (stopResult.isFailure && picResult.isFailure){
            Result.failure(Exception("릴레이 중단과 사진 등록에 모두 실패했습니다."))
        } else if(picResult.isFailure) {
            Result.failure(Exception("사진 등록에 실패했습니다."))
        } else {
            Result.success(true)
        }
    }
}