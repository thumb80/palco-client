package it.antonino.palco.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import it.antonino.palco.BuildConfig
import it.antonino.palco.common.SinglettonHolderThreeInput
import it.antonino.palco.model.Artist
import it.antonino.palco.model.City
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetworkRepository(
    private val networkAPI: NetworkAPI,
    private val discogsAPI: DiscogsAPI,
    private val unsplashAPI: UnsplashAPI
) {

    companion object : SinglettonHolderThreeInput<NetworkRepository, NetworkAPI, DiscogsAPI, UnsplashAPI>(::NetworkRepository)

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

    fun getConcertiStranieri() : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiStranieri().enqueue(
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

    fun getConcertiNazionaliByMonth(mese: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionaliByMonth(mese).enqueue(
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

    fun getConcertiNazionaliByMonthAndCity(mese: String, city: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionaliByMonthAndCity(mese,city).enqueue(
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

    fun getConcertiNazionaliByTime(time: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionaliByTime(time).enqueue(
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

    fun getConcertiNazionaliByTimeAndCity(time: String, city: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiNazionaliByTimeAndCity(time,city).enqueue(
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

    fun getCities() : MutableLiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        val response = networkAPI.getCities().enqueue(
            object : Callback<ArrayList<City?>?> {
                override fun onFailure(call: Call<ArrayList<City?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<City?>?>,
                    response: Response<ArrayList<City?>?>
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

    fun getNationalArtists() : MutableLiveData<ArrayList<Artist?>?> {
        var responseObject = MutableLiveData<ArrayList<Artist?>?>()
        val response = networkAPI.getNationalArtists().enqueue(
            object : Callback<ArrayList<Artist?>?> {
                override fun onFailure(call: Call<ArrayList<Artist?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Artist?>?>,
                    response: Response<ArrayList<Artist?>?>
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

    fun getConcertiStranieriByMonth(mese: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiStranieriByMonth(mese).enqueue(
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

    fun getConcertiStranieriByMonthAndCity(mese: String, city: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiStranieriByMonthAndCity(mese,city).enqueue(
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

    fun getConcertiStranieriByCity(city: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiStranieriByCity(city).enqueue(
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

    fun getConcertiStranieriByArtist(artist: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiStranieriByArtist(artist).enqueue(
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

    fun getConcertiStranieriByTime(time: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiStranieriByTime(time).enqueue(
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

    fun getConcertiStranieriByTimeAndCity(time: String, city: String) : MutableLiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        val response = networkAPI.getConcertiStranieriByTimeAndCity(time,city).enqueue(
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

    fun getStranieriCities() : MutableLiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        val response = networkAPI.getStranieriCities().enqueue(
            object : Callback<ArrayList<City?>?> {
                override fun onFailure(call: Call<ArrayList<City?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<City?>?>,
                    response: Response<ArrayList<City?>?>
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

    fun getStranierilArtists() : MutableLiveData<ArrayList<Artist?>?> {
        var responseObject = MutableLiveData<ArrayList<Artist?>?>()
        val response = networkAPI.getStranierilArtists().enqueue(
            object : Callback<ArrayList<Artist?>?> {
                override fun onFailure(call: Call<ArrayList<Artist?>?>, t: Throwable) {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${t.toString()}")
                    responseObject.postValue(arrayListOf())
                }

                override fun onResponse(
                    call: Call<ArrayList<Artist?>?>,
                    response: Response<ArrayList<Artist?>?>
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