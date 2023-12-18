package it.antonino.palco.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.launch

class SharedViewModel(
    private val networkRepository: NetworkRepository
): ViewModel() {

    private var _concerti: MutableLiveData<ArrayList<Concerto?>?> = MutableLiveData()
    val concerti: LiveData<ArrayList<Concerto?>?> get() = _concerti

    fun getConcertiNazionali(): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionali()
        }
        return responseObject
    }

    fun getArtistThumb(artist: String?) : LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getArtistThumb(artist)
        }
        return responseObject
    }

    fun getNationalCities(): LiveData<ArrayList<String?>?> {
        var responseObject = MutableLiveData<ArrayList<String?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getCities()
        }
        return responseObject
    }

    fun getNationalArtists(): LiveData<ArrayList<String?>?> {
        var responseObject = MutableLiveData<ArrayList<String?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getNationalArtists()
        }
        return responseObject
    }

    fun getNationalConcertsByCity(city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByCity(city)
        }
        return responseObject
    }

    fun getNationalConcertsByArtist(artist: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByArtist(artist)
        }
        return responseObject
    }

    fun getArtistInfos(artist: String?): LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getArtistInfos(artist)
        }
        return responseObject
    }

    fun setConcerti(concerti: ArrayList<Concerto?>?) {
        _concerti.postValue(concerti)
    }
}
