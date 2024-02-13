package com.gdd.domain.model.history

data class History(
    val projectId: Long,
    val projectName: String,
    val projectIsDone: Boolean,
    val createDate: String, // yyyy-MM-dd HH:mm
    val endDate: String,
    val totalDistance: Int,
    val totalContributor: Int
): Comparable<History>{
    override fun compareTo(other: History): Int {
        return createDate.compareTo(other.createDate)
    }

}
