package com.gdd.data.mapper

import com.gdd.data.model.signin.SignInResponse
import com.gdd.domain.User

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