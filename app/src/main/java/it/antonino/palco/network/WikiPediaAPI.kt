package it.antonino.palco.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiPediaAPI {

    @GET("https://it.wikipedia.org/w/api.php?format=json&origin=*&action=query&prop=extracts&exintro&explaintext&redirects=1&")
    fun getArtistInfos(
        @Query("titles") artist: String?
    ) : Call<JsonObject>
}