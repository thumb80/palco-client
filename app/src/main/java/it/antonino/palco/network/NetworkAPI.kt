package it.antonino.palco.network

import com.google.gson.JsonObject
import it.antonino.palco.model.User
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.Artist
import it.antonino.palco.model.City
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NetworkAPI {

    @POST("registration")
    fun registration(@Body user: User): Call<JsonObject>

    @GET("concerti")
    fun getConcertiNazionali(): Call<ArrayList<Concerto?>?>

    @GET("concertistranieri")
    fun getConcertiStranieri(): Call<ArrayList<Concerto?>?>

    @GET("concerti/{mese}")
    fun getConcertiNazionaliByMonth(
        @Path("mese") mese: String
    ): Call<ArrayList<Concerto?>?>

    @GET("concerti/{mese}/{city}")
    fun  getConcertiNazionaliByMonthAndCity(
        @Path("mese") mese: String,
        @Path("city") city: String
    ): Call<ArrayList<Concerto?>?>

    @GET("concerticitta/{city}")
    fun getConcertiNazionaliByCity(@Path("city") city: String): Call<ArrayList<Concerto?>?>

    @GET("concertiartisti/{artist}")
    fun getConcertiNazionaliByArtist(@Path("artist") artist: String): Call<ArrayList<Concerto?>?>

    @GET("concertidettaglio/{time}")
    fun getConcertiNazionaliByTime(@Path("time") time: String): Call<ArrayList<Concerto?>?>

    @GET("concertidettaglio/{time}/{city}")
    fun getConcertiNazionaliByTimeAndCity(
        @Path("time") time: String,
        @Path("city") city: String
    ): Call<ArrayList<Concerto?>?>

    @GET("cities")
    fun getCities(): Call<ArrayList<City?>?>

    @GET("artists")
    fun getNationalArtists(): Call<ArrayList<Artist?>?>

    @GET("concertistranieri/{mese}")
    fun getConcertiStranieriByMonth(@Path("mese") mese: String): Call<ArrayList<Concerto?>?>

    @GET("concertistranieri/{mese}/{city}")
    fun  getConcertiStranieriByMonthAndCity(
        @Path("mese") mese: String,
        @Path("city") city: String
    ): Call<ArrayList<Concerto?>?>

    @GET("concertistraniericitta/{city}")
    fun getConcertiStranieriByCity(@Path("city") city: String): Call<ArrayList<Concerto?>?>

    @GET("concertistranieriartisti/{artist}")
    fun getConcertiStranieriByArtist(@Path("artist") artist: String): Call<ArrayList<Concerto?>?>

    @GET("concertistranieridettaglio/{time}")
    fun getConcertiStranieriByTime(@Path("time") time: String): Call<ArrayList<Concerto?>?>

    @GET("concertistranieridettaglio/{time}/{city}")
    fun getConcertiStranieriByTimeAndCity(
        @Path("time") time: String,
        @Path("city") city: String
    ): Call<ArrayList<Concerto?>?>

    @GET("straniericities")
    fun getStranieriCities(): Call<ArrayList<City?>?>

    @GET("stranieriartists")
    fun getStranierilArtists(): Call<ArrayList<Artist?>?>

}
