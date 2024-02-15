package com.gdd.retrofit_adapter

import android.util.Log
import okhttp3.Request
import okio.IOException
import okio.Timeout
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

private const val TAG = "ResultCall_Genseong"
class ResultCall<T>(
    private val callDelegate: Call<T>
) : Call<Result<T>> {

    override fun enqueue(callback: Callback<Result<T>>) =
        callDelegate.enqueue(object : Callback<T> {
            override fun onResponse(
                call: Call<T>,
                response: Response<T>
            ) {
                val body = response.body()
                if (response.isSuccessful) {
                    if (body != null) {
                        // body and data is not null
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(Result.success(body))
                        )
                    } else {
                        callback.onResponse(
                            this@ResultCall,
                            Response.success(Result.failure(NullPointerException("response body is null")))
                        )
                    }
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val message = try {
                        if (!errorBodyString.isNullOrBlank()) {
                            JSONObject(errorBodyString).getString("message")
                        } else {
                            ""
                        }
                    } catch (e: Exception) {
                        ""
                    }
                    callback.onResponse(
                        this@ResultCall,
                        Response.success(Result.failure(RelplException(response.code(),message)))
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

    override fun clone(): Call<Result<T>> {
        return ResultCall(callDelegate.clone())
    }

    override fun execute(): Response<Result<T>> {
        val response = callDelegate.execute()
        return if (response.isSuccessful && response.body() != null ) {
            Response.success(Result.success(response.body()!!))
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