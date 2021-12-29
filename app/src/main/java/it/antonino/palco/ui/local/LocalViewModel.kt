package it.antonino.palco.ui.local

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.*
import com.google.gson.JsonObject
import it.antonino.palco.PalcoApplication
import it.antonino.palco.model.City
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.Password
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.collections.ArrayList

class LocalViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    val _city: MediatorLiveData<String> = MediatorLiveData()
    val city: LiveData<String> = _city

    private val executorService: ExecutorService = PalcoApplication.instance.executorService

    private var sharedPreferences: SharedPreferences? = null

    fun getCities(): LiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getCities()
        }
        return responseObject
    }

    fun getConcertiNazionaliByCity(city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByCity(city)
        }
        return responseObject
    }

    fun getArtistThumb(artist: String) : LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getArtistThumb(artist)
        }
        return responseObject
    }

}