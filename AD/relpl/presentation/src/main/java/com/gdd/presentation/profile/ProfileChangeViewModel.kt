package com.gdd.presentation.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.user.ChangeProfileUseCase
import com.gdd.domain.usecase.user.NicknameDuplicatedCheckUseCase
import com.gdd.domain.usecase.user.PhoneDuplicatedCheckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

private const val TAG = "ProfileChangeViewModel_Genseong"
@HiltViewModel
class ProfileChangeViewModel @Inject constructor(
    private val changeProfileUseCase: ChangeProfileUseCase,
    private val nicknameDuplicatedCheckUseCase: NicknameDuplicatedCheckUseCase,
    private val phoneDuplicatedCheckUseCase: PhoneDuplicatedCheckUseCase
): ViewModel(){
    var originNickname = ""
    var originalPhone = ""
    var verificationId = ""

    val newNickname = MutableLiveData<String>()
    val newPhone = MutableLiveData<String>()

    private val _profileChangeResult = MutableLiveData<Result<Boolean>>()
    val profileChangeResult: LiveData<Result<Boolean>>
        get() = _profileChangeResult

    private val _nicknameDupResult = MutableLiveData<Result<Boolean>>()
    val nicknameDupResult: LiveData<Result<Boolean>>
        get() = _nicknameDupResult

    private val _phoneDupResult = MutableLiveData<Result<Boolean>>()
    val phoneDupResult : LiveData<Result<Boolean>>
        get() = _phoneDupResult

    fun updateProfile(userProfilePhoto: File?, userId: Long, userNickname: String, userPhone: String){
        viewModelScope.launch {
            changeProfileUseCase(userProfilePhoto, userId, userNickname, userPhone).let {
                _profileChangeResult.postValue(it)
            }
        }
    }

    fun setInitialValue(nickname: String, phone: String){
        newNickname.postValue(nickname)
        newPhone.postValue(phone.substring(3))
    }

    fun isDuplicatedNickname(nickname: String){
        Log.d(TAG, "isDuplicatedNickname: $nickname")
        viewModelScope.launch {
            nicknameDuplicatedCheckUseCase(nickname).let {
                _nicknameDupResult.postValue(it)
            }
        }
    }

    fun isDuplicatedPhone(phone: String){
        Log.d(TAG, "isDuplicatedPhone: $phone")
        viewModelScope.launch {
            phoneDuplicatedCheckUseCase(phone).let {
                _phoneDupResult.postValue(it)
            }
        }
    }


}