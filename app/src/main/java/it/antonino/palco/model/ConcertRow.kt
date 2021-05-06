package it.antonino.palco.model

data class ConcertRow(var artist: String?, var place: String?, var city: String?, var time: String?, var artistThumb: String?) {

    fun addArtistThumb(url: String?) {
        artistThumb = url
    }

}