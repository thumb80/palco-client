package it.antonino.palco.model

import kotlinx.serialization.Serializable

@Serializable
data class Concerto (
    var artist: String,
    var city: String,
    var place: String,
    var time: String
)

