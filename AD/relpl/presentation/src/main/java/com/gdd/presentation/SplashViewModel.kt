package com.gdd.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.user.User
import com.gdd.domain.usecase.user.AutoLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val autoLoginUseCase: AutoLoginUseCase
): ViewModel(){
    private val _autoLogin = MutableLiveData<Result<User>>()
    val autoLogin: LiveData<Result<User>>
        get() = _autoLogin

    fun autoLogin(userId: Long){
        viewModelScope.launch {
            autoLoginUseCase(userId).let {
                _autoLogin.postValue(it)
            }
        }
    }

}