package com.gdd.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.model.user.User
import com.gdd.domain.usecase.fcm.RegistFcmUseCase
import com.gdd.domain.usecase.history.GetHistoryDetailUseCase
import com.gdd.domain.usecase.user.ReloadUserInfoUseCase
import com.gdd.presentation.base.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getHistoryDetailUseCase: GetHistoryDetailUseCase,
    private val reloadUserInfoUseCase: ReloadUserInfoUseCase,
    private val getFcmUseCase: RegistFcmUseCase
): ViewModel() {
    lateinit var user: User
    var historySelectedProjectId = -1L

    private val _fcmResult = MutableLiveData<Result<Boolean>>()
    val fcmResult: LiveData<Result<Boolean>>
        get() = _fcmResult

    @Inject
    lateinit var prefManager: PrefManager

    private val _historyDetailResult = MutableLiveData<Result<HistoryDetailInfo>?>()
    val historyDetailResult: LiveData<Result<HistoryDetailInfo>?>
        get() = _historyDetailResult

    private val _userInfoResult = MutableLiveData<Result<User>>()
    val userInfoResult: LiveData<Result<User>>
        get() = _userInfoResult

    fun loadHistoryDetail(projectId: Long){
        viewModelScope.launch {
            getHistoryDetailUseCase(projectId).let {
                _historyDetailResult.postValue(it)
            }
        }
    }

    fun clearHistoryDetail(){
        _historyDetailResult.postValue(null)
    }

    fun reloadUserInfo(userId: Long){
        viewModelScope.launch {
            reloadUserInfoUseCase(userId).let {
                _userInfoResult.postValue(it)
            }
        }
    }

    fun registFcmToken(userId: Long, fcmToken: String){
        viewModelScope.launch {
            getFcmUseCase(userId, fcmToken).let {
                _fcmResult.postValue(it)
            }
        }
    }
}