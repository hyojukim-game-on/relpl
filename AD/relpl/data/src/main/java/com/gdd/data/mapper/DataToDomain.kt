package com.gdd.data.mapper

import com.gdd.data.model.point.PointRecordItemResponse
import com.gdd.data.model.point.PointRecordResponse
import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupResponse
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