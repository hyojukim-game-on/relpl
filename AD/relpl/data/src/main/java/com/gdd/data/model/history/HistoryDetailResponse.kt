package com.gdd.data.model.history

import com.gdd.data.model.PointResponse

data class HistoryDetailResponse(
    val userNickname : String,
    val movePath : List<PointResponse>,
    val moveStart : String, // yyyy-MM-dd HH:mm
    val moveEnd : String, //  yyyy-MM-dd HH:mm
    val moveDistance : Int,
    val moveTime : String, // VARCHAR(30)
    val moveMemo : String, //VARCHAR(300)
    val moveContribution : Int, // 서버 계산 필요
    val moveImage : String
)
