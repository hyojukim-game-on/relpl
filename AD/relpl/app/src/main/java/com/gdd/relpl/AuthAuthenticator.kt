package com.gdd.relpl

import com.gdd.data.api.UserService
import com.gdd.data.model.token.ReissueRequest
import com.gdd.data.toNonDefault
import com.gdd.presentation.PrefManager
import com.gdd.relpl.module.NetworkModule
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    @NetworkModule.AuthUserService private val userService: UserService,
    private val prefManager: PrefManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = prefManager.getAccessToken()
        val refreshToken = prefManager.getRefreshToken()

        if (accessToken == null || refreshToken == null){
            return null
        }

        return runBlocking {
            val tokenResponse = userService.reissueToken(ReissueRequest(
                accessToken, refreshToken
            ))

            if (!tokenResponse.isSuccessful || tokenResponse.body() == null) {
                prefManager.deleteAll()
                null
            } else {
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.body()!!.data.accessToken}")
                    .build()
            }
        }
    }
}
