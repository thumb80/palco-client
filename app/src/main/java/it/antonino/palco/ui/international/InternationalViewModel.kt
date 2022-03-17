package it.antonino.palco.ui.international

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import it.antonino.palco.model.City
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.launch

class InternationalViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    fun getConcertiInternazionaliByCity(city: String): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieriByCity(city)
        }
        return responseObject
    }

    fun getCities(): LiveData<ArrayList<City?>?> {
        var responseObject = MutableLiveData<ArrayList<City?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getStranieriCities()
        }
        return responseObject
    }

}