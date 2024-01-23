package com.gdd.presentation.signup

import android.util.Patterns
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject


@HiltViewModel
class SignupViewModel @Inject constructor(

): ViewModel() {
    var phoneNumber = ""
    var id = ""
    var pw = ""
    var nickname = ""
    var verificationId = ""

    private val _phoneValidateResult = MutableLiveData<Boolean>()
    val phoneValidateResult: LiveData<Boolean>
        get() = _phoneValidateResult

    private val _idValidateResult = MutableLiveData<Boolean>()
    val idValidateResult: LiveData<Boolean>
        get() = _idValidateResult

    private val _pwValidateResult = MutableLiveData<Boolean>()
    val pwValidateResult: LiveData<Boolean>
        get() = _pwValidateResult

    private val _nicknameValidateResult = MutableLiveData<Boolean>()
    val nicknameValidateResult: LiveData<Boolean>
        get() = _nicknameValidateResult

    private val _phoneDupResult = MutableLiveData<Boolean>()
    val phoneDupResult: LiveData<Boolean>
        get() = _phoneDupResult


    private val _idDupResult = MutableLiveData<Boolean>()
    val idDupResult: LiveData<Boolean>
        get() = _idDupResult

    private val _nicknameDupResult = MutableLiveData<Boolean>()
    val nicknameDupResult: LiveData<Boolean>
        get() = _nicknameDupResult


    fun isValidPhone(phone: String){
        _phoneValidateResult.value =  phone.length == 8 && phone.isDigitsOnly()
    }

    fun isValidId(id: String){
        _idValidateResult.value = Pattern.matches(id, ID_REG)
    }

    fun isValidPw(pw: String){
        _pwValidateResult.value = Pattern.matches(pw, PW_REG)
    }

    fun isValidNickname(nickname: String){
        _nicknameValidateResult.value = Pattern.matches(nickname, NICKNAME_REG)
    }

    fun isDuplicatedPhone(phone: String){
        viewModelScope.launch {
            try {

            } catch (e: Exception){

            }
        }
    }

    fun isDuplicatedId(id: String){
        viewModelScope.launch {
            try {

            } catch (e: Exception){

            }
        }
    }

    fun isDuplicatedNickname(nickname: String){
        viewModelScope.launch {
            try {

            } catch (e: Exception){

            }
        }
    }

    fun signUp(){
        viewModelScope.launch {
            try {

            } catch (e: Exception){

            }
        }
    }

    companion object{
        const val ID_REG = "^[A-Za-z0-9!@#$%^&*()_]{6,}\$"
        const val PW_REG = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{10,25}$"
        const val NICKNAME_REG = "^[가-힣A-Za-z0-9]*\$"
    }
}