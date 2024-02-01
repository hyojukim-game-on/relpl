package com.d101.data.utils

import android.util.Log
import com.gdd.presentation.PrefManager
import com.google.gson.JsonObject
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

private const val TAG = "AuthInterceptor_Genseong"
class AuthInterceptor @Inject constructor(
    private val prefManager: PrefManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val accessToken = prefManager.getAccessToken()

        if (accessToken != null) {
            request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }
        Log.i("okhttp.after_intercept", "$request")
        return chain.proceed(request)
    }
}
