package it.antonino.palco.model

import com.google.gson.annotations.SerializedName

data class Password (
    @SerializedName("password")
    var password: String
)
