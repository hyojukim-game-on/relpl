package com.gdd.retrofit_adapter

class RelplException(
    val code: Int,
    override val message: String
):Exception(message)