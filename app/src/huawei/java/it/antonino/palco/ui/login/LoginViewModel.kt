package it.antonino.palco.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.antonino.palco.model.User
import it.antonino.palco.repository.HuaweiRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val networkRepository: HuaweiRepository
) : ViewModel() {

    fun registration(user: User): LiveData<Pair<Int?, String?>> {
        var responseObject = MutableLiveData<Pair<Int?,String?>>()
        viewModelScope.launch {
            responseObject = networkRepository.doRegister(user)
        }
        return responseObject
    }

}