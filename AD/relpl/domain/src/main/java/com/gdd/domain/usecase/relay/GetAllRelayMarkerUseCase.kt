package com.gdd.domain.usecase.relay

import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class GetAllRelayMarkerUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(): Result<List<RelayMarker>>{
        return projectRepository.getAllRelayMarker()
    }
}