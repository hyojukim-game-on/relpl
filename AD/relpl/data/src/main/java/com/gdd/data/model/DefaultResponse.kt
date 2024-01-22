package com.gdd.data.model

class DefaultResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)
