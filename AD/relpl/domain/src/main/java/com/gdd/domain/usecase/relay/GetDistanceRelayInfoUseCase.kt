package com.gdd.domain.usecase.relay

import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class GetDistanceRelayInfoUseCase  @Inject constructor(
    private val projectRepository: ProjectRepository
){
    suspend operator fun invoke(projectId: Long): Result<DistanceRelayInfo>{
        return projectRepository.getDistanceProjectInfo(projectId)
    }
}