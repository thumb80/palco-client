package it.antonino.palco.`interface`

import com.google.gson.JsonObject
import it.antonino.palco.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkInterface {

    @POST("registration")
    fun registration(@Body user: User): Call<JsonObject>

    @POST("uploadHuaweiToken")
    fun uploadHuaweiToken(@Body user: User): Call<JsonObject>

}