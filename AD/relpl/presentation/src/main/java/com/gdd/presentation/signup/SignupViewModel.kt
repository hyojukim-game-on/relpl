package com.gdd.presentation.signup

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.user.SignUpResult
import com.gdd.domain.usecase.user.IdDuplicatedCheckUseCase
import com.gdd.domain.usecase.user.NicknameDuplicatedCheckUseCase
import com.gdd.domain.usecase.user.PhoneDuplicatedCheckUseCase
import com.gdd.domain.usecase.user.RegisterProfilePhotoUseCase
import com.gdd.domain.usecase.user.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.math.log


private const val TAG = "SignupViewModel_Genseong"
@HiltViewModel
class SignupViewModel @Inject constructor(
    private val idDuplicatedCheckUseCase: IdDuplicatedCheckUseCase,
    private val nickDuplicatedCheckUseCase: NicknameDuplicatedCheckUseCase,
    private val phoneDuplicatedCheckUseCase: PhoneDuplicatedCheckUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val registerProfilePhotoUseCase: RegisterProfilePhotoUseCase
): ViewModel() {
    var phoneNumber = ""
    var id = ""
    var pw = ""
    var nickname = ""
    var verificationId = ""

    var userId = -1L
    var userNickname = ""

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

    private val _phoneDupResult = MutableLiveData<Result<Boolean>>()
    val phoneDupResult: LiveData<Result<Boolean>>
        get() = _phoneDupResult


    private val _idDupResult = MutableLiveData<Result<Boolean>>()
    val idDupResult: LiveData<Result<Boolean>>
        get() = _idDupResult

    private val _nicknameDupResult = MutableLiveData<Result<Boolean>>()
    val nicknameDupResult: LiveData<Result<Boolean>>
        get() = _nicknameDupResult

    private val _signUpResult = MutableLiveData<Result<SignUpResult>>()
    val signUpResult: LiveData<Result<SignUpResult>>
        get() = _signUpResult

    private val _registerPhotoResult = MutableLiveData<Result<Boolean>>()
    val registerPhotoResult : LiveData<Result<Boolean>>
        get() = _registerPhotoResult


    fun isValidPhone(phone: String){
        _phoneValidateResult.value =  phone.length == 8 && phone.isDigitsOnly()
    }

    fun isValidId(id: String){
        _idValidateResult.value = Pattern.matches(ID_REG, id)
    }

    fun isValidPw(pw: String){
        _pwValidateResult.value = Pattern.matches(PW_REG, pw)
    }

    fun isValidNickname(nickname: String){
        _nicknameValidateResult.value = Pattern.matches(NICKNAME_REG, nickname)
    }

    fun isDuplicatedPhone(phone: String){
//        this@SignupViewModel.phoneNumber = phone
        viewModelScope.launch {
            phoneDuplicatedCheckUseCase.invoke(phone).let {
                if (it.isSuccess){
                    this@SignupViewModel.phoneNumber = phone
                }
                _phoneDupResult.postValue(it)
            }
        }
    }

    fun isDuplicatedId(id: String){
        Log.d(TAG, "isDuplicatedId: $id")
        viewModelScope.launch {
            idDuplicatedCheckUseCase(id).let {
                if (it.isSuccess){
                    this@SignupViewModel.id = id
                    Log.d(TAG, "isDuplicatedId!: ${this@SignupViewModel.id}")
                }
                _idDupResult.postValue(it)
            }
        }
    }

    fun isDuplicatedNickname(nickname: String){
        viewModelScope.launch {
            nickDuplicatedCheckUseCase(nickname).let {
                if (it.isSuccess){
                    this@SignupViewModel.nickname = nickname
                }
                _nicknameDupResult.postValue(it)
            }
        }
    }

    fun signUp(){
        viewModelScope.launch {
            Log.d(TAG, "signUp: $id")
            signUpUseCase(phoneNumber, id, pw, nickname).let {
                _signUpResult.postValue(it)
            }
        }
    }

    fun registerProfilePhoto(file: File, userId: Long){
        viewModelScope.launch {
            registerProfilePhotoUseCase(file, userId).let {
                _registerPhotoResult.postValue(it)
            }
        }
    }

    companion object{
        const val ID_REG = "^[a-zA-Z0-9_.]{6,20}\$"
        const val PW_REG = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&.])[A-Za-z[0-9]$@$!%*#?&.]{10,20}$"
        const val NICKNAME_REG = "^[가-힣A-Za-z0-9]{2,15}$"
    }
}