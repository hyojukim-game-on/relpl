package com.gdd.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.user.User
import com.gdd.domain.usecase.fcm.RegistFcmUseCase
import com.gdd.domain.usecase.user.SignInUseCase
import com.gdd.presentation.Event
import com.gdd.presentation.base.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: SignInUseCase,
    private val prefManager: PrefManager,
    private val getFcmUseCase: RegistFcmUseCase
): ViewModel() {
    var _id = MutableLiveData<String>()
    var _password = MutableLiveData<String>()

    private var _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>>
        get() = _loginResult

    private var _inputErrorString = MutableLiveData<Event<Boolean>>()
    val inputErrorString: LiveData<Event<Boolean>>
        get() = _inputErrorString



    fun login(){
        viewModelScope.launch {

            if (checkInputValidate()){
                loginUseCase(
                    _id.value!!,
                    _password.value!!
                ).let {
                    it.getOrNull()?.let { user ->
                        prefManager.setUserId(user.id)
                        prefManager.setAccessToken(user.accessToken)
                        prefManager.setRefreshToken(user.refreshToken)
                    }
                    _loginResult.postValue(it)
                }
            }
        }
    }

    private fun checkInputValidate(): Boolean{
        val id = _id.value ?: ""
        val password = _password.value ?: ""

        return if (id.isNotBlank() && password.isNotBlank()){
            _inputErrorString.value = Event(true)
            true
        } else {
            _inputErrorString.value = Event(false)
            false
        }
    }

}