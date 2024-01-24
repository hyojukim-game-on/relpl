package com.gdd.data.retrofit

import com.gdd.data.model.DefaultResponse
import com.squareup.moshi.rawType
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResponseAdapter<T, R>(
    private val successType: Type,
): CallAdapter<T, Call<Result<R?>>> {
    override fun responseType() = DefaultResponse(0,"",successType)::class.java

    override fun adapt(call: Call<T>): Call<Result<R?>> {
        return ResultCall(call)
    }
}