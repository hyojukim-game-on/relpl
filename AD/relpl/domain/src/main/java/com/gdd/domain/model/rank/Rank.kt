package com.gdd.domain.model.rank

data class Rank(
    val dailyRanking: List<RankItem>,
    val weeklyRanking: List<RankItem>,
    val monthlyRanking: List<RankItem>,
)