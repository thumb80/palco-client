package it.antonino.palco.network

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRepository(
    private val discogsAPI: DiscogsAPI,
    private val wikipediaAPI: WikiPediaAPI
) {

    companion object : SingletonHolder<
            NetworkRepository,
            DiscogsAPI,
            WikiPediaAPI
            >(::NetworkRepository)


    fun getArtistThumb(artist: String?): MutableLiveData<JsonObject?> {
        val responseObject = MutableLiveData<JsonObject?>()
        discogsAPI.getArtistThumb(artist).enqueue(
            object : Callback<JsonObject?> {
                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    responseObject.postValue(null)
                }

                override fun onResponse(
                    call: Call<JsonObject?>,
                    response: Response<JsonObject?>
                ) {
                    responseObject.postValue(response.body())
                }

            }
        )
        return responseObject
    }

    fun getArtistInfos(artist: String?): MutableLiveData<JsonObject?> {
        val responseObject = MutableLiveData<JsonObject?>()
        wikipediaAPI.getArtistInfos(artist).enqueue(
            object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    responseObject.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    responseObject.postValue(null)
                }

            }
        )
        return responseObject
    }
}

open class SingletonHolder<out T, in A,B>(creator: (A, B) -> T) {

    private var creator: ((A,B) -> T)? = creator
    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A, arg2: B): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg,arg2)
                instance = created
                creator = null
                created
            }
        }
    }

}
