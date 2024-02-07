package com.gdd.domain.usecase.relay

import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class JoinRelayUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(projectId: Long): Result<Long>{
        return projectRepository.joinRelay(projectId)
    }
}