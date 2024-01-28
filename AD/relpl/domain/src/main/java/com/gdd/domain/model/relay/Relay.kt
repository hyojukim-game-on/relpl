package com.gdd.domain.model.relay

data class Relay(
    val projectId: Int,
    val projectName: String,
    val projectIsdone: Boolean,
    val moveStart: String, //yyyy-MM-dd HH:mm
    val moveEnd: String,
    val moveDistance: Int,
    val moveTime: Int
): Comparable<Relay>{
    override fun compareTo(other: Relay): Int {
        return other.moveEnd.compareTo(moveEnd)
    }

}
