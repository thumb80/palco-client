package it.antonino.palco.ui.international

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import it.antonino.palco.model.Concerto
import it.antonino.palco.network.NetworkRepository
import kotlinx.coroutines.launch

class InternationalViewModel(
    private val networkRepository: NetworkRepository
) : ViewModel() {

    fun getConcertiInternazionali(): LiveData<ArrayList<Concerto?>?> {
        var responseObject = MutableLiveData<ArrayList<Concerto?>?>()
        viewModelScope.launch {
            responseObject = networkRepository.getConcertiStranieri()
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