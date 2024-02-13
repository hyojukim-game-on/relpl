package com.gdd.domain.model.history

import com.gdd.domain.model.Point

data class HistoryDetail (
    val userNickname : String,
    val movePath : List<Point>,
    val moveStart : String, // yyyy-MM-dd HH:mm
    val moveEnd : String, //  yyyy-MM-dd HH:mm
    val moveDistance : Int,
    val moveTime : String, // VARCHAR(30)
    val moveMemo : String, //VARCHAR(300)
    val moveContribution : Int, // 서버 계산 필요
    val moveImage : String
)