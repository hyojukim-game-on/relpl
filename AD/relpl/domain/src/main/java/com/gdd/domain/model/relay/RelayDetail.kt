package com.gdd.domain.model.relay

data class RelayDetail(
    val moveStart: String,
    val moveEnd: String,
    val moveDistance: Int,
    val moveMemo: String?,
    val moveContribution: Int
): Comparable<RelayDetail>{
    override fun compareTo(other: RelayDetail): Int {
        return other.moveStart.compareTo(moveStart)
    }

}
