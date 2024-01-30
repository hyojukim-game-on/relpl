package com.gdd.domain.model.history

data class History(
    val projectId: Long,
    val projectName: String,
    val projectIsDone: Boolean,
    val createDate: String, // yyyy-MM-dd HH:mm
    val endDate: String,
    val totalDistance: Int,
    val totalContributor: Int
)
