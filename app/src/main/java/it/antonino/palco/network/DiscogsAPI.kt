package it.antonino.palco.network

import com.google.gson.JsonObject
import it.antonino.palco.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscogsAPI {

    @GET(BuildConfig.discogsSearchURL)
    fun getArtistThumb(
        @Query("q") artist: String?,
        @Query("key") discogsApiKey: String = BuildConfig.dicogsApiKey,
        @Query("secret") discogsSecret: String = BuildConfig.discogsSecret
    ): Call<JsonObject>

}
