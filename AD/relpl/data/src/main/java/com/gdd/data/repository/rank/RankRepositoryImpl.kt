package com.gdd.data.repository.rank

import com.gdd.data.mapper.toRank
import com.gdd.data.repository.rank.remote.RankRemoteDataSource
import com.gdd.domain.model.rank.Rank
import com.gdd.domain.repository.RankRepository
import javax.inject.Inject

class RankRepositoryImpl @Inject constructor(
    private val rankRemoteDataSource: RankRemoteDataSource
): RankRepository {
    override suspend fun getRank(): Result<Rank> {
        return rankRemoteDataSource.getRank().map {
            it.toRank()
        }
    }
}