package com.gdd.domain.repository

import com.gdd.domain.model.rank.Rank

interface RankRepository {
    suspend fun getRank(): Result<Rank>
}