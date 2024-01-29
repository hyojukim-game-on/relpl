package com.gdd.data.mapper

import com.gdd.data.model.history.HistoryResponse
import com.gdd.data.model.point.PointRecordItemResponse
import com.gdd.data.model.point.PointRecordResponse
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupResponse
import com.gdd.domain.model.history.History
import com.gdd.domain.model.point.PointRecord
import com.gdd.domain.model.point.PointRecordListItem
import com.gdd.domain.model.user.SignUpResult
import com.gdd.domain.model.user.User

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