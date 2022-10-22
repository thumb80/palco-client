package it.antonino.palco.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("username")
    val username: String = "",
    @SerializedName("password")
    val password: String?,
    @SerializedName("firebase_token")
    val firebase_token: String?,
    @SerializedName("huawei_token")
    val huawei_token: String?
)
