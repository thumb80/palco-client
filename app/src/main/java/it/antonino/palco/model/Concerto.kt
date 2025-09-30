package it.antonino.palco.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Concerto (
    var artist: String,
    var city: String,
    var place: String,
    var time: String
)

