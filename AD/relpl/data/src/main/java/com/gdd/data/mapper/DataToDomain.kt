package com.gdd.data.mapper

import android.annotation.SuppressLint
import com.gdd.data.model.PointResponse
import com.gdd.data.model.history.HistoryDetailResponse
import com.gdd.data.model.history.HistoryDetailSummeryResponse
import com.gdd.data.model.history.HistoryResponse
import com.gdd.data.model.point.PointRecordItemResponse
import com.gdd.data.model.point.PointRecordResponse
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupResponse
import com.gdd.domain.model.Point
import com.gdd.domain.model.history.History
import com.gdd.domain.model.history.HistoryDetail
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.model.point.PointRecord
import com.gdd.domain.model.point.PointRecordListItem
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

@SuppressLint("SimpleDateFormat")
fun String.toHistoryDetailStartDate(): String{
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
    val date = simpleDateFormat.parse(this)
    val newFormatter = SimpleDateFormat("MM.dd HH:mm")
    return newFormatter.format(date)
}