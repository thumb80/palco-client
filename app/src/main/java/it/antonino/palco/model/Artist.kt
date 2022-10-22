package it.antonino.palco.model

import com.google.gson.annotations.SerializedName

data class Artist(
    @SerializedName("artist")
    private val artist: String = ""
) {
    fun getArtist(): String {
        return artist
    }
}
