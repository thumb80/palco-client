package it.antonino.palco.network

import com.google.gson.JsonObject
import it.antonino.palco.BuildConfig
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DiscogsAPI {

    @GET("/database/search")
    fun getArtistThumb(
        @Query("q") artist: String?,
        @Query("key") discogsApiKey: String = BuildConfig.DiscogsApiKey,
        @Query("secret") discogsSecret: String = BuildConfig.DiscogsSecret
    ): Call<JsonObject>

    @GET("/artists/{labelId}")
    fun getArtistInfos(
        @Path("labelId") labelId: String?
    ): Call<JsonObject>

}
