package com.gdd.data.repository.rank.remote

import com.gdd.data.model.rank.RankResponse

interface RankRemoteDataSource {
    suspend fun getRank(): Result<RankResponse>
}