package com.gdd.presentation.point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.point.GetCurrentPointUseCase
import com.gdd.presentation.base.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointUseViewModel @Inject constructor(
    private val prefManager: PrefManager,
    private val getCurrentPointUseCase: GetCurrentPointUseCase
): ViewModel() {
    private var _nickname = MutableLiveData<String>()
    val nickname: LiveData<String>
        get() = _nickname

    private var _point = MutableLiveData<Int>()
    val point: LiveData<Int>
        get() = _point

    private var _userId = MutableLiveData<Long>()
    val userId: LiveData<Long>
        get() = _userId

    init {
        _userId.value = prefManager.getUserId()
    }

    fun getPointInfo(){
        viewModelScope.launch {
            val userId = prefManager.getUserId()
            if (userId != -1L){
                getCurrentPointUseCase(userId).let {
                    _point.postValue(
                        it.getOrDefault(-1)
                    )
                }
            }
        }
    }

    fun setNickname(nickname: String){
        _nickname.value = nickname
    }
}