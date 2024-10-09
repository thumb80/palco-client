package it.antonino.palco.model

import com.google.gson.annotations.SerializedName

class Concerto (
    @SerializedName("artist")
    var artist: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("place")
    var place: String,
    @SerializedName("time")
    var time: String
)

