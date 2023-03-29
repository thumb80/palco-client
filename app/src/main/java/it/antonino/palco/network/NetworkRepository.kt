package it.antonino.palco.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import it.antonino.palco.BuildConfig
import it.antonino.palco.common.SingletonHolderThreeInput
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.DateSearchDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRepository(
    private val networkAPI: NetworkAPI,
    private val discogsAPI: DiscogsAPI,
    private val unsplashAPI: UnsplashAPI
) {

    companion object : SingletonHolderThreeInput<
            NetworkRepository,
            NetworkAPI,
            DiscogsAPI,
            UnsplashAPI
            >(::NetworkRepository)

    val TAG = NetworkRepository::class.java.simpleName

    fun getConcertiNazionali() : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionali().enqueue(
            object : Callback<ArrayList<Concerto?>?> {
                override fun onFailure(call: Call<ArrayList<Concerto?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Concerto?>?>,
                    response: Response<ArrayList<Concerto?>?>
                ) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

    fun getConcertiNazionaliByMonth(dateSearchDTO: DateSearchDTO) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionaliByMonth(dateSearchDTO).enqueue(
            object : Callback<ArrayList<Concerto?>?> {
                override fun onFailure(call: Call<ArrayList<Concerto?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Concerto?>?>,
                    response: Response<ArrayList<Concerto?>?>
                ) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

    fun getConcertiNazionaliByCity(city: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionaliByCity(city).enqueue(
            object : Callback<ArrayList<Concerto?>?> {
                override fun onFailure(call: Call<ArrayList<Concerto?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Concerto?>?>,
                    response: Response<ArrayList<Concerto?>?>
                ) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

    fun getConcertiNazionaliByArtist(artist: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionaliByArtist(artist).enqueue(
            object : Callback<ArrayList<Concerto?>?> {
                override fun onFailure(call: Call<ArrayList<Concerto?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Concerto?>?>,
                    response: Response<ArrayList<Concerto?>?>
                ) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

    fun getCities() : MutableLiveData<ArrayList<String?>?> {
        var responseObject = MutableLiveData<ArrayList<String?>?>()
        val response = networkAPI.getCities().enqueue(
            object : Callback<ArrayList<String?>?> {
                override fun onFailure(call: Call<ArrayList<String?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<String?>?>,
                    response: Response<ArrayList<String?>?>
                ) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

    fun getNationalArtists() : MutableLiveData<ArrayList<String?>?> {
        var responseObject = MutableLiveData<ArrayList<String?>?>()
        val response = networkAPI.getNationalArtists().enqueue(
            object : Callback<ArrayList<String?>?> {
                override fun onFailure(call: Call<ArrayList<String?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<String?>?>,
                    response: Response<ArrayList<String?>?>
                ) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

    fun getArtistThumb(artist: String?) : MutableLiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        val response = discogsAPI.getArtistThumb(artist).enqueue(
            object : Callback<JsonObject?> {
                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(null)
                }

                override fun onResponse(
                    call: Call<JsonObject?>,
                    response: Response<JsonObject?>
                ) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

    fun getPlacePhoto(place: String) : MutableLiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        val response = unsplashAPI.getPhoto(
            "Client-ID ${BuildConfig.UNSPLASH_ACCESS_KEY}",
            place
        ).enqueue(
            object : Callback<JsonObject?> {
                override fun onResponse(call: Call<JsonObject?>, response: Response<JsonObject?>) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response}")
                    responseObject.postValue(response.body())
                }

                override fun onFailure(call: Call<JsonObject?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(null)
                }

            }
        )
        Log.d(TAG, "$response")
        return responseObject
    }

}
