package com.gdd.domain.usecase.point

import com.gdd.domain.model.point.PointRecord
import com.gdd.domain.repository.UserRepository
import javax.inject.Inject

class GetPointRecordUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Result<PointRecord>{
        return userRepository.getPointRecord(userId)
    }
}