package com.gdd.domain.usecase.history

import com.gdd.domain.model.history.History
import com.gdd.domain.model.history.HistoryInfo
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<HistoryInfo>{
        return userRepository.getHistory(userId)
    }
}