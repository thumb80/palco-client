package it.antonino.palco.network

import com.google.gson.JsonObject
import it.antonino.palco.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiPediaAPI {

    @GET(BuildConfig.wikipediaSearchURL)
    fun getArtistInfos(
        @Query("titles") artist: String?
    ) : Call<JsonObject>
}