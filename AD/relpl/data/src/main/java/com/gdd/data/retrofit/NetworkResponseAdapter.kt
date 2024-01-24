package com.gdd.data.retrofit

import com.gdd.data.model.DefaultResponse
import com.squareup.moshi.rawType
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class NetworkResponseAdapter<T>(
    private val successType: Type,
): CallAdapter<DefaultResponse<T>, Call<Result<T?>>> {
    override fun responseType() = DefaultResponse(0, "",successType)::class.java.also { println(it) }

    override fun adapt(call: Call<DefaultResponse<T>>): Call<Result<T?>> {
        return ResultCall(call)
    }
}