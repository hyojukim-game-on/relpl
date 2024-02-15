package com.gdd.presentation.point

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.point.PointRecordListItem
import com.gdd.domain.usecase.point.GetPointRecordUseCase
import com.gdd.presentation.base.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointRecordViewModel @Inject constructor(
    private val prefManager: PrefManager,
    private val getPointRecordUseCase: GetPointRecordUseCase
) : ViewModel() {
    private var _nickname = MutableLiveData<String>()
    val nickname: LiveData<String>
        get() = _nickname

    private var _totalPoint = MutableLiveData<Int>()
    val totalPoint: LiveData<Int>
        get() = _totalPoint

    private var _pointRecordList = MutableLiveData<Result<List<PointRecordListItem>>>()
    val pointRecordList: LiveData<Result<List<PointRecordListItem>>>
        get() = _pointRecordList

    fun getPointRecord() {
        val userId = prefManager.getUserId()
        if (userId != -1L) {
            viewModelScope.launch {
                getPointRecordUseCase(userId).getOrNull()?.let {
                    _totalPoint.postValue(it.totalCoin)
                    _pointRecordList.postValue(Result.success(it.recordList))
                } ?: kotlin.run {
                    _totalPoint.postValue(-1)
                    _pointRecordList.postValue(Result.failure(Throwable()))
                }
            }
        } else {
            _totalPoint.postValue(-1)
            _pointRecordList.postValue(Result.failure(Throwable()))
        }
    }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }
}