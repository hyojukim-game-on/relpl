package com.gdd.data.model.history

data class HistoryDetailSummeryResponse(
    val projectName: String,
    val projectDistance: Int,
    val projectTime: Int,
    val projectPeople: Int,
    val detailList: List<HistoryDetailResponse>
)
