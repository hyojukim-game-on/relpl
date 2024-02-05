package com.gdd.domain.usecase.relay

import com.gdd.domain.model.Point
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class CreateDistanceRelayUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
){
    suspend operator fun invoke(
        userId: Long,
        projectName: String,
        projectCreateDate: String,
        projectEndDate: String,
        projectTotalDistance: Int,
        projectStartCoordinate: Point
    ): Result<Long>{
        return projectRepository.createDistanceRelay(
            userId,
            projectName,
            projectCreateDate,
            projectEndDate,
            projectTotalDistance,
            projectStartCoordinate
        )
    }
}