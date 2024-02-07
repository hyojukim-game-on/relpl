package com.gdd.domain.usecase.relay

import com.gdd.domain.model.relay.RelayInfoData
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class GetRelayInfoUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke():RelayInfoData{
        return projectRepository.getProjectInfo()
    }
}