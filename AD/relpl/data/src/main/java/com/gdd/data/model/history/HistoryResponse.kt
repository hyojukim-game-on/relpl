package com.gdd.data.model.history

data class HistoryResponse(
    val projectId: Long,
    val projectName: String,
    val projectIsDone: Boolean,
    val createDate: String, // yyyy-MM-dd HH:mm
    val endDate: String,
    val totalDistance: Int,
    val totalContributor: Int
)
