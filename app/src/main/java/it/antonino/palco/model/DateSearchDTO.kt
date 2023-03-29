package it.antonino.palco.model

import com.google.gson.annotations.SerializedName

data class DateSearchDTO (
    @SerializedName("startDate")
    private val startDate: String = "",
    @SerializedName("endDate")
    private val endDate: String = ""
) {
    fun getStartDate(): String {
        return this.startDate
    }
    fun getEndDate(): String {
        return this.endDate
    }
}
