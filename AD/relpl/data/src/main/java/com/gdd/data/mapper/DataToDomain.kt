package com.gdd.data.mapper

import android.annotation.SuppressLint
import com.gdd.data.model.PointResponse
import com.gdd.data.model.history.HistoryDetailResponse
import com.gdd.data.model.history.HistoryDetailSummeryResponse
import com.gdd.data.model.history.HistoryResponse
import com.gdd.data.model.history.HistorySummeryResponse
import com.gdd.data.model.point.PointRecordItemResponse
import com.gdd.data.model.point.PointRecordResponse
import com.gdd.data.model.project.DistanceProjectResponse
import com.gdd.data.model.project.MarkerResponse
import com.gdd.data.model.rank.RankResponse
import com.gdd.data.model.rank.RankResponseItem
import com.gdd.data.model.report.ReportRecordResponse
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupResponse
import com.gdd.domain.model.Point
import com.gdd.domain.model.history.History
import com.gdd.domain.model.history.HistoryDetail
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.model.history.HistoryInfo
import com.gdd.domain.model.point.PointRecord
import com.gdd.domain.model.point.PointRecordListItem
import com.gdd.domain.model.rank.Rank
import com.gdd.domain.model.rank.RankItem
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.model.report.ReportRecord
import com.gdd.domain.model.user.SignUpResult
import com.gdd.domain.model.user.User
import java.text.SimpleDateFormat

fun SignInResponse.toUser(): User {
    return User(
        userId,
        userNickname,
        userTotalCoin,
        userTotalDistance,
        userTotalReport,
        userPhone,
        userImage,
        accessToken,
        refreshToken,
    )
}

fun SignupResponse.toSignUpResult(): SignUpResult {
    return SignUpResult(userId, userNickname)
}

fun PointRecordItemResponse.toPointRecordListItem(): PointRecordListItem {
    return PointRecordListItem(
        coinEventDate, coinAmount, coinEventDetail
    )
}

fun PointRecordResponse.toPointRecord(): PointRecord {
    return PointRecord(
        userTotalCoin,
        eventList.map { it.toPointRecordListItem() }
    )
}

fun List<HistoryResponse>.toHistoryList(): List<History>{
    return this.map {
         History(
             it.projectId,
             it.projectName,
             it.projectIsDone,
             it.createDate.substring(0, 10),
             it.endDate.substring(0, 10),
             it.totalDistance,
             it.totalContributor
         )
    }
}

fun HistorySummeryResponse.toHistoryInfo(): HistoryInfo{
    return HistoryInfo(
        this.totalProject,
        this.userTotalDistance,
        this.userTotalTime,
        this.detailList.toHistoryList()
    )
}

fun PointResponse.toPoint(): Point{
    return Point(this.x, this.y)
}

fun HistoryDetailResponse.toHistoryDetail(): HistoryDetail{
    return HistoryDetail(
        this.userNickname,
        this.movePath.map {
            it.toPoint()
        },
        this.moveStart.toHistoryDetailStartDate(),
        this.moveEnd.toHistoryDetailStartDate(),
        this.moveDistance,
        this.moveTime,
        this.moveMemo,
        this.moveContribution,
        this.moveImage
    )
}
fun HistoryDetailSummeryResponse.toHistoryDetailInfo(): HistoryDetailInfo{
    return HistoryDetailInfo(
        this.projectName,
        this.projectDistance,
        this.projectTime,
        this.projectPeople,
        this.detailList.map {
            it.toHistoryDetail()
        }
    )
}



fun DistanceProjectResponse.toDistanceRelayInfo(): DistanceRelayInfo{
    return DistanceRelayInfo(
        this.projectId,
        this.projectName,
        this.totalContributor,
        this.totalDistance.toStringDistance(),
        this.remainDistance.toStringDistance(),
        this.createDate.toKoreanDateFormat(),
        this.endDate.toKoreanDateFormat(),
        this.isPath,
        this.stopCoordinate.toPoint(),
        this.progress,
        this.memo ?: "메모가 없습니다"
    )
}

fun MarkerResponse.toMarker(): RelayMarker{
    return RelayMarker(
        this.projectId,
        this.stopCoordinate.toPoint(),
        this.isPath
    )
}

fun ReportRecordResponse.toReportRecord(): ReportRecord {
    return ReportRecord(
        reportDate,
        Point(
            reportCoordinate.x,
            reportCoordinate.y
        )
    )
}

fun RankResponse.toRank():Rank {
    return Rank(
        dailyRanking.map { it.toRankItem() },
        weeklyRanking.map { it.toRankItem() },
        monthlyRanking.map { it.toRankItem() }
    )
}

fun RankResponseItem.toRankItem(): RankItem{
    return RankItem(nickname, distance.toInt())
}


// 여기부터 dto -> dto가 아닌 형식 변환의 mapper들 입니다
@SuppressLint("SimpleDateFormat")
fun String.toHistoryDetailStartDate(): String{
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = simpleDateFormat.parse(this)
    val newFormatter = SimpleDateFormat("MM.dd HH:mm")
    return newFormatter.format(date)
}

fun Int.toStringDistance(): String{
    val km = if (this / 1000 > 0) "${this/100}km" else ""
    val m = "${this%1000}m"
    return "$km $m"
}

fun String.toKoreanDateFormat(): String{
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = simpleDateFormat.parse(this)
    val koreanFormatter = SimpleDateFormat("yyyy년 M월 dd일")
    return koreanFormatter.format(date)
}

fun Int.toKoreanProgress(): String{
    return "현재 ${this}% 진행됐습니다"
}
