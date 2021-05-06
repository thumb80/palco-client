package it.antonino.palco.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("username")
    private val username: String = "",
    @SerializedName("password")
    private val password: String?,
    @SerializedName("firebase_token")
    private val firebase_token: String?,
    @SerializedName("huawei_token")
    private val huawei_token: String?
)