package com.gdd.presentation.point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PointUseViewModel @Inject constructor(

): ViewModel() {
    private var _nickname = MutableLiveData<String>()
    val nickname: LiveData<String>
        get() = _nickname

    private var _point = MutableLiveData<Int>()
    val point: LiveData<Int>
        get() = _point


}