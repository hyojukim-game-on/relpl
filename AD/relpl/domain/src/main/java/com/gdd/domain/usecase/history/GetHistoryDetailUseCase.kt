package com.gdd.domain.usecase.history

import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class GetHistoryDetailUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(projectId: Long): Result<HistoryDetailInfo>{
        return userRepository.getHistoryDetail(projectId)
    }
}