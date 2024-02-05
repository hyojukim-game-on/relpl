package com.gdd.domain.usecase.relay

import com.gdd.domain.model.relay.IsExistDistanceRelay
import com.gdd.domain.repository.ProjectRepository
import javax.inject.Inject

class IsExistDistanceRelayUseCase @Inject constructor(
    private val projectRepository: ProjectRepository
){
    suspend operator fun invoke(lat: Double, lng: Double): Result<IsExistDistanceRelay>{
        return projectRepository.isExistDistanceRelay(lat, lng)
    }
}