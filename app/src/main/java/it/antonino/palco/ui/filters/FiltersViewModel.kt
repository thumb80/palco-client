package it.antonino.palco.ui.filters

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.antonino.palco.model.Artist
import it.antonino.palco.model.City
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.FiltersUiState
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FiltersViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel()  {

    private val _uiState = MutableStateFlow(FiltersUiState(false, null))
    val uiState: StateFlow<FiltersUiState> = _uiState.asStateFlow()

    private val _concerts: MutableLiveData<ArrayList<Concerto?>> = MutableLiveData()
    val concerts: LiveData<ArrayList<Concerto?>> = _concerts

    private val _fillConcerts: MutableLiveData<Boolean> = MutableLiveData(false)
    val fillConcerts: LiveData<Boolean> = _fillConcerts

    private val _cities: MutableLiveData<Boolean> = MutableLiveData()
    val cities: LiveData<Boolean> = _cities

    private val _artists: MutableLiveData<Boolean> = MutableLiveData()
    val artists: LiveData<Boolean> = _artists

    fun postFillConcerts(value: Boolean) {
        _fillConcerts.postValue(value)
    }

    var concertsSize: Int = _concerts.value?.size ?: 0

    var concertsSizeObservable = object : ObservableField<String>(concertsSize.toString()) {
        override fun set(value: String?) {
            super.set(value)
            // a value has been set
            concertsSize = value?.toIntOrNull() ?: concertsSize
        }

        override fun get(): String {
            return concertsSize.toString()
        }
    }


    fun getConcertiNazionaliByMonth(month: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByMonth(month)
        }
        _uiState.value = _uiState.value.copy(true, responseObject.value)
        return responseObject
    }

    fun getConcertiStranieriByMonth(month: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByMonth(month)
            responseObject.value?.let { _concerts.value?.addAll(it.toCollection(ArrayList())) }
        }
        return responseObject
    }

    fun getConcertiNazionaliByCity(city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByCity(city)
            responseObject.value?.let { _concerts.value?.addAll(it.toCollection(ArrayList())) }
        }
        return responseObject
    }

    fun getConcertiStranieriByCity(city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByCity(city)
            responseObject.value?.let { _concerts.value?.addAll(it.toCollection(ArrayList())) }
        }
        return responseObject
    }

    fun getConcertiNazionaliByMonthAndCity(month: String, city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByMonthAndCity(month, city)
            responseObject.value?.let { _concerts.value?.addAll(it.toCollection(ArrayList())) }
        }
        return responseObject
    }

    fun getConcertiStranieriByMonthAndCity(month: String, city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByMonthAndCity(month, city)
            responseObject.value?.let { _concerts.value?.addAll(it.toCollection(ArrayList())) }
        }
        return responseObject
    }

    fun getConcertiNazionaliByArtist(artist: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiNazionaliByArtist(artist)
            responseObject.value?.let { _concerts.value?.addAll(it.toCollection(ArrayList())) }
        }
        return responseObject
    }

    fun getConcertiStranieriByArtist(artist: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByArtist(artist)
            responseObject.value?.let { _concerts.value?.addAll(it.toCollection(ArrayList())) }
        }
        return responseObject
    }

    fun getNationalArtists(): LiveData<ArrayList<Artist?>?> {
        var responseObject = MutableLiveData<ArrayList<Artist?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getNationalArtists()
            _artists.postValue(true)

        }
        return responseObject
    }

    fun getStranieriArtists(): LiveData<ArrayList<Artist?>?> {
        var responseObject = MutableLiveData<ArrayList<Artist?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getStranieriArtists()
            _artists.postValue(true)
        }
        return responseObject
    }

    fun getNationalCities(): LiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getCities()
            _cities.postValue(true)
        }
        return responseObject
    }

    fun getStranieriCities(): LiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getStranieriCities()
            _cities.postValue(true)
        }
        return responseObject
    }

}