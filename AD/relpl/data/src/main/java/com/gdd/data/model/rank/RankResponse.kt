package com.gdd.data.model.rank

data class RankResponse(
    val dailyRanking: List<RankResponseItem>,
    val weeklyRanking: List<RankResponseItem>,
    val monthlyRanking: List<RankResponseItem>,
)

