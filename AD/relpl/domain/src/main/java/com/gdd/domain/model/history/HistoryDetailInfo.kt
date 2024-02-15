package com.gdd.domain.model.history

data class HistoryDetailInfo(
    val projectName: String,
    val projectDistance: Int,
    val projectTime: Int,
    val projectPeople: Int,
    val detailList: List<HistoryDetail>
)
