package com.gdd.data.retrofit

class RelplException(
    val code: Int,
    override val message: String
):Exception(message)