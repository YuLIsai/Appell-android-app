package com.yivg.appell.Utils.Models

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("token") val token: String,
    @SerializedName("message") val message: String
)
