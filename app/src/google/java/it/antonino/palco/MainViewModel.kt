package it.antonino.palco

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.antonino.palco.model.User
import it.antonino.palco.repository.GoogleRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val networkRepository: GoogleRepository
) : ViewModel() {

    fun uploadFirebaseToken(user: User) : LiveData<String?> {
        var responseObject = MutableLiveData<String?>()
        viewModelScope.launch {
            responseObject = networkRepository.uploadFirebaseToken(user)
        }
        return responseObject
    }

}