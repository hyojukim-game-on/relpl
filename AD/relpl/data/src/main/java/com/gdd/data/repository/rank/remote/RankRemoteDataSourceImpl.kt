package com.gdd.data.repository.rank.remote

import com.gdd.data.api.RankService
import com.gdd.data.model.rank.RankResponse
import com.gdd.data.toNonDefault
import javax.inject.Inject

class RankRemoteDataSourceImpl @Inject constructor(
    private val rankService: RankService
): RankRemoteDataSource {
    override suspend fun getRank(): Result<RankResponse> {
        return rankService.getRank().toNonDefault()
    }
}