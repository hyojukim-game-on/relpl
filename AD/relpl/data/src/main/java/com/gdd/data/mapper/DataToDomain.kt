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
import com.gdd.data.model.project.IsExistDistanceResponse
import com.gdd.data.model.project.MarkerResponse
import com.gdd.data.model.project.PathProjectResponse
import com.gdd.data.model.project.ProjectInfoEntity
import com.gdd.data.model.project.RecommendPathResponse
import com.gdd.data.model.rank.RankResponse
import com.gdd.data.model.rank.RankResponseItem
import com.gdd.data.model.report.ReportRecordResponse
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupResponse
import com.gdd.data.model.tracking.LocationTrackingEntity
import com.gdd.data.model.tracking.RelayPathEntity
import com.gdd.domain.model.Point
import com.gdd.domain.model.tracking.TrackingData
import com.gdd.domain.model.history.History
import com.gdd.domain.model.history.HistoryDetail
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.model.history.HistoryInfo
import com.gdd.domain.model.point.PointRecord
import com.gdd.domain.model.point.PointRecordListItem
import com.gdd.domain.model.rank.Rank
import com.gdd.domain.model.rank.RankItem
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.IsExistDistanceRelay
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.model.relay.RelayInfoData
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.model.report.ReportRecord
import com.gdd.domain.model.tracking.RelayPathData
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
//        this.moveStart.toHistoryDetailStartDate(),
//        this.moveEnd.toHistoryDetailStartDate(),
        this.moveStart,
        this.moveEnd,
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
        this.projectTotalContributer,
        this.projectTotalDistance,
        this.projectRemainingDistance,
        this.projectCreateDate.toShortKoreanFormat(),
        this.projectEndDate.toShortKoreanFormat(),
        this.projectIsPath,
        this.projectStopCoordinate.toPoint(),
        this.progress,
        this.userMoveMemo ?: "메모가 없습니다"
    )
}

fun PathProjectResponse.toPathRelayInfo(): PathRelayInfo{
    return PathRelayInfo(
        this.projectId,
        this.projectName,
        this.projectTotalContributer,
        this.projectTotalDistance,
        this.projectRemainingDistance,
        this.projectCreateDate.toShortKoreanFormat(),
        this.projectEndDate.toShortKoreanFormat(),
        this.projectIsPath,
        this.projectStopCoordinate.toPoint(),
        this.projectProgress,
        this.userMoveMemo ?: "메모가 없습니다",
        this.projectRoute.map { it.toPoint() }
    )
}

fun MarkerResponse.toMarker(): RelayMarker{
    return RelayMarker(
        this.projectId,
        this.stopCoordinate.toPoint(),
        this.path
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

fun LocationTrackingEntity.toTrackData(): TrackingData {
    return TrackingData(milliTime, longitude, latitude)
}

fun RelayPathEntity.toRelayPathData(): RelayPathData{
    return RelayPathData(latitude, longitude, myVisit, beforeVisit)
}

fun RecommendPathResponse.toRecommendedPath(): RecommendedPath{
    return RecommendedPath(
        this.shortestId,
        this.shortestTotalDistance,
        this.shortestPath.map {
            it.toPoint()
        },
        this.recommendId,
        this.recommendTotalDistance,
        this.recommendPath.map {
            it.toPoint()
        }
    )
}

fun IsExistDistanceResponse.toIsExistDistanceRelay(): IsExistDistanceRelay{
    return IsExistDistanceRelay(
        this.exist,
        this.projectId,
        this.startCoordinate.toPoint()
    )
}

fun ProjectInfoEntity.toRelayInfoData(): RelayInfoData{
    return RelayInfoData(
        id,name,totalContributer,totalDistance,remainDistance,createDate,endDate,isPath,Point(endLongitude,endLatitude)
    )
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
    val km = if (this / 1000 > 0) "${this/1000}km" else ""
    val m = "${this%1000}m"
    return "$km $m"
}

fun String.toKoreanDateFormat(): String{
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = simpleDateFormat.parse(this)
    val koreanFormatter = SimpleDateFormat("yyyy년 M월 dd일")
    return koreanFormatter.format(date)
}

fun String.toShortKoreanFormat(): String{
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = simpleDateFormat.parse(this)
    val koreanFormatter = SimpleDateFormat("yyyy년 M월 dd일")
    return koreanFormatter.format(date)
}

fun Int.toKoreanProgress(): String{
    return "현재 ${this}% 진행됐습니다"
}
