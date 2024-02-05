package com.gdd.domain.usecase.rank

import com.gdd.domain.model.rank.Rank
import com.gdd.domain.repository.RankRepository
import javax.inject.Inject

class GetRankUseCase @Inject constructor(
    private val rankRepository: RankRepository
) {
    suspend operator fun invoke(): Result<Rank>{
        return rankRepository.getRank()
    }
}