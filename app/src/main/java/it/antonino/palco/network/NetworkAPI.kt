package it.antonino.palco.network

import it.antonino.palco.BuildConfig
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.DateSearchDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkAPI {

    @Headers("x-api-key:bad777b9-f1f1-49de-bf7b-a10607788108")
    @GET("getconcerti")
    fun getConcertiNazionali(): Call<ArrayList<Concerto?>?>

    @Headers("x-api-key:bad777b9-f1f1-49de-bf7b-a10607788108")
    @POST("getconcertimese")
    fun getConcertiNazionaliByMonth(
        @Body dateSearchDTO: DateSearchDTO
    ): Call<ArrayList<Concerto?>?>

    @Headers("x-api-key:bad777b9-f1f1-49de-bf7b-a10607788108")
    @GET("getconcerticity/{city}")
    fun getConcertiNazionaliByCity(@Path("city") city: String): Call<ArrayList<Concerto?>?>

    @Headers("x-api-key:bad777b9-f1f1-49de-bf7b-a10607788108")
    @GET("getconcertiartist/{artist}")
    fun getConcertiNazionaliByArtist(@Path("artist") artist: String): Call<ArrayList<Concerto?>?>

    @Headers("x-api-key:bad777b9-f1f1-49de-bf7b-a10607788108")
    @GET("getcities")
    fun getCities(): Call<ArrayList<String?>?>

    @Headers("x-api-key:bad777b9-f1f1-49de-bf7b-a10607788108")
    @GET("getartists")
    fun getNationalArtists(): Call<ArrayList<String?>?>

}
