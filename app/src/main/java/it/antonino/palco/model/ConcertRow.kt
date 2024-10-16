package it.antonino.palco.model

import java.util.*

data class ConcertRow(
    var artist: String?,
    var city: String?,
    var place: String?,
    var time: Date?,
    var artistThumb: String?,
    var artistInfo: String?
    ) {

    fun addArtistThumb(url: String?) {
        artistThumb = url
    }

    fun addArtistInfo(info: String?) {
        artistInfo = info
    }
}
