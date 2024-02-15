package com.gdd.domain.model.history

data class HistoryInfo(
    val totalProject: Int,
    val userTotalDistance: Int,
    val userTotalTime: Int,
    val detailList: List<History>
)
