package com.gdd.data.retrofit

import com.gdd.data.model.DefaultResponse
import okhttp3.Request
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ResultCall<T,R>(
    private val callDelegate: Call<T>
) : Call<Result<R?>> {

    override fun enqueue(callback: Callback<Result<R?>>) =
        callDelegate.enqueue(object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>
            ) {
                if (response.body() !is DefaultResponse<*>){
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(TypeCastException("reponse type is not DefaultResponse")))
                    )
                }
                println(response)
                val body = response.body() as DefaultResponse<*>

                if (response.isSuccessful) {
                    if (body != null) {
                        // body and data is not null
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(Result.success(body.data as R))
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

            override fun onFailure(call: Call<T>, t: Throwable) {
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

    override fun clone(): Call<Result<R?>> {
        return ResultCall(callDelegate.clone())
    }

    override fun execute(): Response<Result<R?>> {
        val response = callDelegate.execute()
        return if (response.isSuccessful && response.body() != null && response.body() is DefaultResponse<*> ) {
            Response.success(Result.success((response.body() as DefaultResponse<*>).data as R))
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