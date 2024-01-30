package com.gdd.relpl

import com.gdd.data.model.DefaultResponse
import com.gdd.data.model.token.ReissueResponse
import com.gdd.data.repository.user.remote.UserRemoteDataSource
import com.gdd.presentation.PrefManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenInterceptor @Inject constructor(private val userRemoteDataSource: UserRemoteDataSource): Interceptor {

    @Inject
    lateinit var prefManager: PrefManager
    override fun intercept(chain: Interceptor.Chain): Response {
        /* Request Header에 accessToken을 추가한다. */
        val accessToken: String = prefManager.getAccessToken()?: ""
        val request = chain.request().newBuilder().addHeader("Authorization", accessToken).build()
        var response: Response = chain.proceed(request)

        /* 토큰 유효 시간 만료 에러 발생 */
        if (response.code == 410) {
            /* synchronized: 괄호 안에 작성된 코드는 동기적 프로그래밍 방식으로 실행된다. */
            synchronized(this) {
                /* 기존 API 호출 시 사용된 accessToken과 EncryptedSharedPreferences에 사용된 accessToken을 비교한다. */
                val newAccessToken: String = prefManager.getAccessToken()?: ""

                /* 같으면 토큰 재발급 API 호출한다. */
                if (accessToken==newAccessToken) {
                    val refreshToken: String = prefManager.getRefreshToken()?: ""
                    val reissueResponse: Result<DefaultResponse<ReissueResponse>> = runBlocking {
                        userRemoteDataSource.reissueToken(accessToken, refreshToken)
                    }
                    return if (reissueResponse.isSuccess){
                        reissueResponse.getOrNull()!!.let {
                            prefManager.setAccessToken(it.data.accessToken!!)
                            prefManager.setRefreshToken(it.data.refreshToken!!)

                            response.close()

                            /* 재발급 받은 accessToken을 활용해 기존 API를 재호출한다. */
                            chain.proceed(chain.request().newBuilder().addHeader("Authorization", it.data.accessToken).build())
                        }
                    }else{
                        response
                    }

                    /*
                    /* 재발급 받은 accessToken과 refreshToken을 EncryptedSharedPreferences에 저장한다. */
                    return if (reissueResponse.data!=null) {
                        prefManager.setAccessToken(reissueResponse.data!!.accessToken)
                        prefManager.setRefreshToken(reissueResponse.data!!.refreshToken)

                        response.close()

                        /* 재발급 받은 accessToken을 활용해 기존 API를 재호출한다. */
                        chain.proceed(chain.request().newBuilder().addHeader("Authorization", reissueResponse.data.accessToken).build())
                    } else {
                        response
                    }

                     */
                }

                /* 다르면 이미 재발급 요청이 완료된 상태이다. 따라서 재발급 받은 accessToken을 활용해 기존 API를 재호출한다. */
                else {
                    response.close()
                    return chain.proceed(chain.request().newBuilder().addHeader("Authorization", newAccessToken).build())
                }
            }
        } else {
            return response
        }
    }
}