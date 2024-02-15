package com.gdd.domain.usecase.relay

import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class GetPathRelayInfoUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
){
    suspend operator fun invoke(projectId: Long): Result<PathRelayInfo>{
        return projectRepository.getPathProjectInfo(projectId)
    }
}