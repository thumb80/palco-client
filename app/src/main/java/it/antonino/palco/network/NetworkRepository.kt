package it.antonino.palco.network

import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import it.antonino.palco.common.SingletonHolder
import it.antonino.palco.model.Concerto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRepository(
    private val networkAPI: NetworkAPI,
    private val discogsAPI: DiscogsAPI,
    private val wikipediaAPI: WikiPediaAPI
) {

    companion object : SingletonHolder<
            NetworkRepository,
            NetworkAPI,
            DiscogsAPI,
            WikiPediaAPI
            >(::NetworkRepository)

    fun getConcertiNazionali(): MutableLiveData<ArrayList<Concerto?>?> {
        val responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        networkAPI.getConcertiNazionali().enqueue(
            object : Callback<ArrayList<Concerto?>?> {
                override fun onFailure(call: Call<ArrayList<Concerto?>?>, t: Throwable) {
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Concerto?>?>,
                    response: Response<ArrayList<Concerto?>?>
                ) {
                    responseObject.postValue(response.body())
                }

            }
        )
        return responseObject
    }

    fun getConcertiNazionaliByCity(city: String): MutableLiveData<ArrayList<Concerto?>?> {
        val responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        networkAPI.getConcertiNazionaliByCity(city).enqueue(
            object : Callback<ArrayList<Concerto?>?> {
                override fun onFailure(call: Call<ArrayList<Concerto?>?>, t: Throwable) {
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Concerto?>?>,
                    response: Response<ArrayList<Concerto?>?>
                ) {
                    responseObject.postValue(response.body())
                }

            }
        )
        return responseObject
    }

    fun getConcertiNazionaliByArtist(artist: String): MutableLiveData<ArrayList<Concerto?>?> {
        val responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        networkAPI.getConcertiNazionaliByArtist(artist).enqueue(
            object : Callback<ArrayList<Concerto?>?> {
                override fun onFailure(call: Call<ArrayList<Concerto?>?>, t: Throwable) {
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Concerto?>?>,
                    response: Response<ArrayList<Concerto?>?>
                ) {
                    responseObject.postValue(response.body())
                }

            }
        )
        return responseObject
    }

    fun getCities(): MutableLiveData<ArrayList<String?>?> {
        val responseObject = MutableLiveData<ArrayList<String?>?>()
        networkAPI.getCities().enqueue(
            object : Callback<ArrayList<String?>?> {
                override fun onFailure(call: Call<ArrayList<String?>?>, t: Throwable) {
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<String?>?>,
                    response: Response<ArrayList<String?>?>
                ) {
                    responseObject.postValue(response.body())
                }

            }
        )
        return responseObject
    }

    fun getNationalArtists(): MutableLiveData<ArrayList<String?>?> {
        val responseObject = MutableLiveData<ArrayList<String?>?>()
        networkAPI.getNationalArtists().enqueue(
            object : Callback<ArrayList<String?>?> {
                override fun onFailure(call: Call<ArrayList<String?>?>, t: Throwable) {
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<String?>?>,
                    response: Response<ArrayList<String?>?>
                ) {
                    responseObject.postValue(response.body())
                }

            }
        )
        return responseObject
    }

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
