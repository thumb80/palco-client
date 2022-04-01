package it.antonino.palco.ui.national

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import it.antonino.palco.model.Concerto
import it.antonino.palco.model.Password
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.launch

class NationalViewModel(
    private val networkRepository: NetworkRepository
)   : ViewModel() {

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

}