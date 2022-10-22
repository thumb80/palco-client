package it.antonino.palco.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import it.antonino.palco.model.Artist
import it.antonino.palco.model.City
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.launch

class SharedViewModel(
    private val networkRepository: NetworkRepository
): ViewModel() {

    fun getConcertiNazionali(): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionali()
        }
        return responseObject
    }

    fun getConcertiInternazionali(): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieri()
        }
        return responseObject
    }

    fun getPlacePhoto(place: String) : LiveData<JsonObject?> {
        var responseObject = MutableLiveData<JsonObject?>()
        viewModelScope.launch {
            responseObject = networkRepository.getPlacePhoto(place)
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

    fun getNationalCities(): LiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getCities()
        }
        return responseObject
    }

    fun getInternationalCities(): LiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getStranieriCities()
        }
        return responseObject
    }

    fun getNationalArtists(): LiveData<ArrayList<Artist?>?> {
        var responseObject = MutableLiveData<ArrayList<Artist?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getNationalArtists()
        }
        return responseObject
    }

    fun getInternationalArtists(): LiveData<ArrayList<Artist?>?> {
        var responseObject = MutableLiveData<ArrayList<Artist?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getStranierilArtists()
        }
        return responseObject
    }

    fun getNationalConcertsByMonth(month: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByMonth(month)
        }
        return responseObject
    }

    fun getInternationalConcertsByMonth(month: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByMonth(month)
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

    fun getInternationalConcertsByCity(city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByCity(city)
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

    fun getInternationalConcertsByArtist(artist: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByArtist(artist)
        }
        return responseObject
    }


}
