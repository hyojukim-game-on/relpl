package com.gdd.data.mapper

import com.gdd.data.model.signin.SignInResponse
import com.gdd.data.model.signup.SignupResponse
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
    return SignUpResult(userId,userNickname)
}