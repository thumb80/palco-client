package it.antonino.palco.network

import com.google.gson.JsonObject
import it.antonino.palco.model.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkAPI {

    @POST("registration")
    fun registration(@Body user: User): Call<JsonObject>

    @POST("concerti")
    fun getConcertiNazionali(@Body password: Password): Call<ArrayList<Concerto?>?>

    @POST("concertistranieri")
    fun getConcertiStranieri(): Call<ArrayList<Concerto?>?>

    @POST("concerti/{mese}")
    fun getConcertiNazionaliByMonth(@Path("mese") mese: String): Call<ArrayList<Concerto?>?>

    @POST("concerti/{mese}/{city}")
    fun  getConcertiNazionaliByMonthAndCity(@Path("mese") mese: String, @Path("city") city: String): Call<ArrayList<Concerto?>?>

    @POST("concerticitta/{city}")
    fun getConcertiNazionaliByCity(@Path("city") city: String,@Body password: Password): Call<ArrayList<Concerto?>?>

    @POST("concertiartisti/{artist}")
    fun getConcertiNazionaliByArtist(@Path("artist") artist: String): Call<ArrayList<Concerto?>?>

    @POST("concertidettaglio/{time}")
    fun getConcertiNazionaliByTime(@Path("time") time: String): Call<ArrayList<Concerto?>?>

    @POST("concertidettaglio/{time}/{city}")
    fun getConcertiNazionaliByTimeAndCity(@Path("time") time: String, @Path("city") city: String): Call<ArrayList<Concerto?>?>

    @POST("cities")
    fun getCities(@Body password: Password): Call<ArrayList<City?>?>

    @POST("artists")
    fun getNationalArtists(): Call<ArrayList<Artist?>?>

    @POST("concertistranieri/{mese}")
    fun getConcertiStranieriByMonth(@Path("mese") mese: String): Call<ArrayList<Concerto?>?>

    @POST("concertistranieri/{mese}/{city}")
    fun  getConcertiStranieriByMonthAndCity(@Path("mese") mese: String, @Path("city") city: String): Call<ArrayList<Concerto?>?>

    @POST("concertistraniericitta/{city}")
    fun getConcertiStranieriByCity(@Path("city") city: String): Call<ArrayList<Concerto?>?>

    @POST("concertistranieriartisti/{artist}")
    fun getConcertiStranieriByArtist(@Path("artist") artist: String): Call<ArrayList<Concerto?>?>

    @POST("concertistranieridettaglio/{time}")
    fun getConcertiStranieriByTime(@Path("time") time: String): Call<ArrayList<Concerto?>?>

    @POST("concertistranieridettaglio/{time}/{city}")
    fun getConcertiStranieriByTimeAndCity(@Path("time") time: String, @Path("city") city: String): Call<ArrayList<Concerto?>?>

    @POST("straniericities")
    fun getStranieriCities(): Call<ArrayList<City?>?>

    @POST("stranieriartists")
    fun getStranierilArtists(): Call<ArrayList<Artist?>?>

}