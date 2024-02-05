package com.gdd.data.api

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.rank.RankResponse
import retrofit2.http.GET

interface RankService {
    @GET("rank/now")
    suspend fun getRank(): Result<DefaultResponse<RankResponse>>
}