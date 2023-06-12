package com.yivg.appell.Utils

data class userModel(
    val message: String,
    val user: User
)

data class User(
    val created_at: String,
    val email: String,
    val email_verified_at: Any,
    val id: Int,
    val name: String,
    val updated_at: String
)