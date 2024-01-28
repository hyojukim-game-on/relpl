package com.gdd.presentation.point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gdd.domain.usecase.point.GetCurrentPointUseCase
import com.gdd.presentation.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun getUserIdForBarcode(){
        prefManager.getUserId().let {
            _userId.value = it
        }
    }

}