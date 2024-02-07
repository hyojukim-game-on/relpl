package com.gdd.domain.usecase.relay

import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class SaveRelayInfoUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(
        id: Long,
        name: String,
        totalContributer: Int,
        totalDistance: Int,
        remainDistance: Int,
        createDate: String,
        endDate: String,
        isPath: Boolean,
        endLatitude: Double,
        endLongitude: Double
    ) {
        projectRepository.saveProjectInfo(
            id,
            name,
            totalContributer,
            totalDistance,
            remainDistance,
            createDate,
            endDate,
            isPath,
            endLatitude,
            endLongitude
        )
    }
}