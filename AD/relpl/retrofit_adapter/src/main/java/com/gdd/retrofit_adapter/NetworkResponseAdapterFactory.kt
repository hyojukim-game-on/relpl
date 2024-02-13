package com.gdd.retrofit_adapter

import com.squareup.moshi.rawType
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.jvm.internal.impl.load.java.JavaClassesTracker.Default

class NetworkResponseAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        println("annotations : ${annotations.size}")
        println("returnType : $returnType")
        if (Call::class.java != getRawType(returnType) || returnType !is ParameterizedType) return null

        val responseType = getParameterUpperBound(0,returnType)

        if (Result::class.java != responseType.rawType || responseType !is ParameterizedType) return null

        val bodyType = getParameterUpperBound(0,responseType)

        return NetworkResponseAdapter<Any>(bodyType)
    }
}