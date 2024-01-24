package com.gdd.data.retrofit

import com.gdd.data.model.DefaultResponse
import okhttp3.Request
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultCall<T>(
    private val callDelegate: Call<DefaultResponse<T>>
) : Call<Result<T?>> {

    override fun enqueue(callback: Callback<Result<T?>>) =
        callDelegate.enqueue(object : Callback<DefaultResponse<T>> {
            override fun onResponse(
                call: Call<DefaultResponse<T>>,
                response: Response<DefaultResponse<T>>
            ) {
                println(response)
                val body = response.body()

                if (response.isSuccessful) {
                    if (body != null) {
                        // body and data is not null
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(Result.success(body.data))
                        )
                    } else {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(Result.failure(NullPointerException("response body is null")))
                        )
                    }
                } else {
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(RelplException(response.code(),response.message())))
                    )
                }
            }

            override fun onFailure(call: Call<DefaultResponse<T>>, t: Throwable) {
                println("fail : ${t}")
                callback.onResponse(
                    this@ResultCall,
                    Response.success(Result.failure(
                        when(t){
                            is IOException -> IOException("네트워크 에러",t)
                            else -> t
                        }
                    ))
                )
            }
        })

    override fun clone(): Call<Result<T?>> {
        return ResultCall(callDelegate.clone())
    }

    override fun execute(): Response<Result<T?>> {
        val response = callDelegate.execute()
        return if (response.isSuccessful && response.body()?.data != null) {
            Response.success(Result.success(response.body()!!.data!!))
        } else {
            Response.error(response.code(), response.errorBody()!!)
        }
    }

    override fun isExecuted(): Boolean {
        return callDelegate.isExecuted
    }

    override fun cancel() {
        callDelegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return callDelegate.isCanceled
    }

    override fun request(): Request {
        return callDelegate.request()
    }

    override fun timeout(): Timeout {
        return callDelegate.timeout()
    }
}