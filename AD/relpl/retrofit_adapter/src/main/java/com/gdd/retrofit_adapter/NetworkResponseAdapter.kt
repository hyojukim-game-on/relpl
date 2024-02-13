package com.gdd.retrofit_adapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class NetworkResponseAdapter<T>(
    private val successType: Type,
): CallAdapter<T, Call<Result<T>>> {
    override fun responseType() = successType

    override fun adapt(call: Call<T>): Call<Result<T>> {
        return ResultCall(call)
    }
}