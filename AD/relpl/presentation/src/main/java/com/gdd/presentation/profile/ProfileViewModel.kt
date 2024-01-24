package com.gdd.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gdd.domain.usecase.user.ChangePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
): ViewModel() {

    private val _pwValidateResult = MutableLiveData<Boolean>()
    val pwValidateResult : LiveData<Boolean>
        get() = _pwValidateResult

    fun isValidPw(pw: String){
        _pwValidateResult.value = Pattern.matches(PW_REG, pw)
    }

    fun changePassword(userId: Long, currentPassword: String, newPassword: String){

    }

    companion object{
        const val PW_REG = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{10,20}$"
    }
}