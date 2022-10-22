package it.antonino.palco.network

import com.google.gson.JsonObject
import it.antonino.palco.BuildConfig
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UnsplashAPI {

    @GET("https://api.unsplash.com/search/photos")
    fun getPhoto(
        @Header("Authorization") accessKey: String,
        @Query("query") query: String
    ) : Call<JsonObject>

}
