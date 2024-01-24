package com.gdd.data.model.profile

data class ChangePasswordRequest(
    val userId: Long,
    val currentPassword: String,
    val newPassword: String
)
