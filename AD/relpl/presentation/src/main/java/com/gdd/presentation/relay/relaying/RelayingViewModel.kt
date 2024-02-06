package com.gdd.presentation.relay.relaying

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.relay.ClearTrackingDataUseCase
import com.gdd.domain.usecase.relay.GetLocationTrackingDataUseCase
import com.gdd.presentation.mapper.DateFormatter
import com.gdd.presentation.model.mapper.toTrackingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RelayingViewModel_Genseong"

@HiltViewModel
class RelayingViewModel @Inject constructor(
    private val getLocationTrackingDataUseCase: GetLocationTrackingDataUseCase,
    private val clearTrackingDataUseCase: ClearTrackingDataUseCase
) : ViewModel() {
    private var firstTime: Long? = null
    private var elapsedTimeFlag = true

    val trackingStateFlow = getLocationTrackingDataUseCase()
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            1
        )
//        .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000),
//            listOf()
//        )
        .map { list ->
            Log.d(TAG, "Viewmodel: trackingStateFlow")
            list.map {
                it.toTrackingPoint()
            }
        }

    private var _remainDistance = MutableLiveData<String>()
    val remainDistance: LiveData<String>
        get() = _remainDistance

    private var _progressDistance = MutableLiveData<String>()
    val progressDistance: LiveData<String>
        get() = _progressDistance

    private var _startTime = MutableLiveData<String>()
    val startTime: LiveData<String>
        get() = _startTime

    private var _elapsedTime = MutableLiveData<String>()
    val elapsedTime: LiveData<String>
        get() = _elapsedTime


    init {
        collectTrackingDataUI()
        countProgressTime()
    }

    private fun collectTrackingDataUI() {
        viewModelScope.launch(Dispatchers.IO) {
            getLocationTrackingDataUseCase().map { list ->
                Log.d(TAG, "Viewmodel: trackingStateFlow")
                list.map {
                    it.toTrackingPoint()
                }
            }.collect { list ->
                // 시작 시간
                if (list.isNotEmpty() && startTime.value == null) {
                    firstTime = list.first().timeMillis
                    _startTime.postValue(DateFormatter.getRelayStartTimeString(list.first().timeMillis))
                }
                // 진행 거리
                _progressDistance.postValue(list.zipWithNext().sumOf {
                    it.first.latLng.distanceTo(it.second.latLng)
                }.toInt().toString() + "m")
            }
        }
    }

    private fun countProgressTime() {
        viewModelScope.launch(Dispatchers.IO) {
            while (elapsedTimeFlag) {
                // 경과 시간
                if (firstTime != null) {
                    val hour = (System.currentTimeMillis() - firstTime!!) / 3600000
                    val minute = (System.currentTimeMillis() - firstTime!!) % 3600000 / 60000
                    _elapsedTime.postValue(
                        "우리 동네를 위한 시간 ${if (hour < 10) "0" else ""}${hour}시간 ${if (minute < 10) "0" else ""}${minute}분"
                    )
                }
                delay(30000)
            }
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            clearTrackingDataUseCase()
            viewModelScope.cancel()
        }
    }
}