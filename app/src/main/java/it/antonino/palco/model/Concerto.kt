package it.antonino.palco.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.sql.Date

data class Concerto(
    @SerializedName("artist")
    private val artist: String = "",
    @SerializedName("time")
    private val time: Date,
    @SerializedName("place")
    private val place: String = "",
    @SerializedName("city")
    private val city: String = ""
): Serializable {

    companion object {
        private const val serialVersionUID = 20180617104400L
    }

    fun getArtist(): String {
        return artist
    }

    fun getTime(): Date {
        return time
    }

    fun getPlace(): String {
        return place
    }

    fun getCity(): String {
        return city
    }

}
