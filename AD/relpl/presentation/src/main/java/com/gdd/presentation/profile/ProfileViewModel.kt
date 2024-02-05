package com.gdd.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.user.ChangePasswordUseCase
import com.gdd.domain.usecase.user.ExitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase,
    private val exitUseCase: ExitUseCase
): ViewModel() {

    private val _pwValidateResult = MutableLiveData<Boolean>()
    val pwValidateResult : LiveData<Boolean>
        get() = _pwValidateResult

    private val _pwChangeResult = MutableLiveData<Result<Boolean>>()
    val pwChangeResult: LiveData<Result<Boolean>>
        get() = _pwChangeResult

    private val _exitResult = MutableLiveData<Result<Boolean>>()
    val exitResult: LiveData<Result<Boolean>>
        get() = _exitResult

    fun isValidPw(pw: String){
        _pwValidateResult.value = Pattern.matches(PW_REG, pw)
    }

    fun changePassword(userId: Long, currentPassword: String, newPassword: String){
       viewModelScope.launch {
           changePasswordUseCase(userId, currentPassword, newPassword).let {
               _pwChangeResult.postValue(it)
           }
       }
    }

    fun exit(userId: Long, userPassword: String){
        viewModelScope.launch {
            exitUseCase(userId, userPassword).let {
                _exitResult.postValue(it)
            }
        }
    }

    companion object{
        const val PW_REG = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{10,20}$"
    }
}