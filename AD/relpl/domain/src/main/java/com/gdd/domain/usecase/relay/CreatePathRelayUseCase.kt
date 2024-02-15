package com.gdd.domain.usecase.relay

import com.gdd.domain.model.Point
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class CreatePathRelayUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(userId: Long,
                                projectSelectedId : String,
                                projectSelectedTotalDistance: Int,
                                projectSelectedCoordinateTotalSize: Int,
                                projectName: String,
                                projectCreateDate: String,
                                projectEndDate: String,
                                projectStartPoint: Point,
                                projectEndPoint: Point
    ): Result<Long>{
        return projectRepository.createPathRelay(
            userId,
            projectSelectedId,
            projectSelectedTotalDistance,
            projectSelectedCoordinateTotalSize,
            projectName,
            projectCreateDate,
            projectEndDate,
            projectStartPoint,
            projectEndPoint
        )
    }
}