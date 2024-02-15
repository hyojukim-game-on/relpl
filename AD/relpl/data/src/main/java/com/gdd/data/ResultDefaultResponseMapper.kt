package com.gdd.data

import com.gdd.data.model.DefaultResponse

fun <T>Result<DefaultResponse<T>>.toNonDefault(): Result<T> {
    return this.map {
        it.data
    }
}