package com.gdd.data.model.history

data class HistorySummeryResponse(
    val totalProject: Int,
    val userTotalDistance: Int,
    val userTotalTime: Int,
    val detailList: List<HistoryResponse>
)
