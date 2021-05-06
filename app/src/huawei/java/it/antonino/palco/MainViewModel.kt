package it.antonino.palco

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import it.antonino.palco.model.User
import it.antonino.palco.repository.HuaweiRepository
import kotlinx.coroutines.launch

class MainViewModel(private val networkRepository : HuaweiRepository): androidx.lifecycle.ViewModel() {

    fun uploadHuaweiToken(user: User) : LiveData<String?> {
        var responseObject = MutableLiveData<String?>()
        viewModelScope.launch {
            responseObject = networkRepository.uploadHuaweiToken(user)
        }
        return responseObject
    }

}