package it.antonino.palco.model

import java.util.*

data class ConcertRow(
    var artist: String?,
    var place: String?,
    var city: String?,
    var time: Date?,
    var artistThumb: String?
    ) {

    fun addArtistThumb(url: String?) {
        artistThumb = url
    }

}
