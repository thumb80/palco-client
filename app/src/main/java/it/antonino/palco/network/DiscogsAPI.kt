package it.antonino.palco.network

import com.google.gson.JsonObject
import it.antonino.palco.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscogsAPI {

    @GET("https://api.discogs.com/database/search")
    fun getArtistThumb(
        @Query("q") artist: String?,
        @Query("key") discogsApiKey: String = BuildConfig.DISCOGS_API_KEY,
        @Query("secret") discogsSecret: String = BuildConfig.DISCOGS_SECRET
    ): Call<JsonObject>

}
