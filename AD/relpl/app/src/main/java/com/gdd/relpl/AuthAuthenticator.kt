package com.gdd.relpl

import android.util.Log
import com.gdd.data.api.UserService
import com.gdd.data.model.token.ReissueRequest
import com.gdd.presentation.base.PrefManager
import com.gdd.relpl.module.NetworkModule
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

private const val TAG = "AuthAuthenticator_Genseong"
class AuthAuthenticator @Inject constructor(
    @NetworkModule.AuthUserService private val userService: UserService,
    private val prefManager: PrefManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        Log.d(TAG, "authenticate: ")
        val userId = prefManager.getUserId()
        val accessToken = prefManager.getAccessToken()
        val refreshToken = prefManager.getRefreshToken()

        if (accessToken == null || refreshToken == null){
            return null
        }

        return runBlocking {
            val tokenResponse = userService.reissueToken(ReissueRequest(
                userId, accessToken, refreshToken
            ))

            if (!tokenResponse.isSuccessful || tokenResponse.body() == null) {
                Log.d(TAG, "authenticate: Fail")
                prefManager.deleteAll()
                null
            } else {
                Log.d(TAG, "authenticate Success: ${tokenResponse.body()!!.data.accessToken}, ${tokenResponse.body()!!.data.refreshToken}")
                prefManager.setAccessToken(tokenResponse.body()!!.data.accessToken)
                prefManager.setRefreshToken(tokenResponse.body()!!.data.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.body()!!.data.accessToken}")
                    .build()
            }
        }
    }
}
