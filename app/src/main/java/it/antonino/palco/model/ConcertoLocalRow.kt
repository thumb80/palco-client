package it.antonino.palco.model

data class ConcertLocalRow(var artist: String?, var place: String?, var time: String?, var bill: String?, var artistThumb: String?) {

    fun addArtistThumb(url: String?) {
        artistThumb = url
    }

}