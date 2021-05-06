package it.antonino.palco.model

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("city")
    private val city: String = ""
) {
    fun getCity(): String {
        return city
    }
}