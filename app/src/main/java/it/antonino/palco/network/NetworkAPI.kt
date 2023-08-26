package it.antonino.palco.network

import it.antonino.palco.BuildConfig
import it.antonino.palco.model.Concerto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface NetworkAPI {

    @Headers("x-api-key:${BuildConfig.authToken}")
    @GET("getconcerti")
    fun getConcertiNazionali(): Call<ArrayList<Concerto?>?>

    @Headers("x-api-key:${BuildConfig.authToken}")
    @GET("getconcerticity/{city}")
    fun getConcertiNazionaliByCity(@Path("city") city: String): Call<ArrayList<Concerto?>?>

    @Headers("x-api-key:${BuildConfig.authToken}")
    @GET("getconcertiartist/{artist}")
    fun getConcertiNazionaliByArtist(@Path("artist") artist: String): Call<ArrayList<Concerto?>?>

    @Headers("x-api-key:${BuildConfig.authToken}")
    @GET("getcities")
    fun getCities(): Call<ArrayList<String?>?>

    @Headers("x-api-key:${BuildConfig.authToken}")
    @GET("getartists")
    fun getNationalArtists(): Call<ArrayList<String?>?>

}
