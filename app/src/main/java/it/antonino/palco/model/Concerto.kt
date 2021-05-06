package it.antonino.palco.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Concerto(
    @SerializedName("artist")
    private val artist: String = "",
    @SerializedName("time")
    private val time: String = "",
    @SerializedName("place")
    private val place: String = "",
    @SerializedName("city")
    private val city: String = "",
    @SerializedName("bill")
    private val bill: String = ""
): Serializable {
    fun getArtist(): String {
        return artist
    }

    fun getTime(): String {
        return time
    }

    fun getPlace(): String {
        return place
    }

    fun getCity(): String {
        return city
    }
    fun getBill(): String {
        return bill
    }
}