package com.gdd.domain.usecase.history

import com.gdd.domain.model.history.History
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<List<History>>{
        return userRepository.getHistory(userId)
    }
}