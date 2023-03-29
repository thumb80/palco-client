package it.antonino.palco.network

import it.antonino.palco.model.Concerto
import it.antonino.palco.model.DateSearchDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkAPI {

    @GET("getconcerti")
    fun getConcertiNazionali(): Call<ArrayList<Concerto?>?>

    @POST("getconcertimese")
    fun getConcertiNazionaliByMonth(
        @Body dateSearchDTO: DateSearchDTO
    ): Call<ArrayList<Concerto?>?>

    @GET("getconcerticity/{city}")
    fun getConcertiNazionaliByCity(@Path("city") city: String): Call<ArrayList<Concerto?>?>

    @GET("getconcertiartist/{artist}")
    fun getConcertiNazionaliByArtist(@Path("artist") artist: String): Call<ArrayList<Concerto?>?>

    @GET("getcities")
    fun getCities(): Call<ArrayList<String?>?>

    @GET("getartists")
    fun getNationalArtists(): Call<ArrayList<String?>?>

}
