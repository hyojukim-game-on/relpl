package com.gdd.presentation.relay.relaying

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.relay.GetRelayInfoUseCase
import com.gdd.domain.usecase.relay.tracking.GetLocationTrackingDataUseCase
import com.gdd.domain.usecase.relay.tracking.GetRelayPathDataUseCase
import com.gdd.presentation.model.RelayInfo
import com.gdd.presentation.model.RelayPath
import com.gdd.presentation.model.TrackingPoint
import com.gdd.presentation.model.mapper.toRelayInfo
import com.gdd.presentation.model.mapper.toRelayPath
import com.gdd.presentation.model.mapper.toTrackingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelayStopInfoViewModel @Inject constructor(
    private val getLocationTrackingDataUseCase: GetLocationTrackingDataUseCase,
    private val getRelayPathDataUseCase: GetRelayPathDataUseCase,
    private val getRelayInfoUseCase: GetRelayInfoUseCase
): ViewModel() {
    private var _locationTrackingPointList = MutableLiveData<List<TrackingPoint>>()
    val locationTrackingPointList: LiveData<List<TrackingPoint>>
        get() = _locationTrackingPointList

    private var _relayPathList = MutableLiveData<List<RelayPath>>()
    val relayPathList: LiveData<List<RelayPath>>
        get() = _relayPathList

    private var _relayInfo = MutableLiveData<RelayInfo>()
    val relayInfo: LiveData<RelayInfo>
        get() = _relayInfo

    private var _callCount = MutableLiveData(0)
    val callCount: LiveData<Int>
        get() = _callCount

    init {
        getRelayPathData()
        getRelayInfo()
        getTrackingData()
    }

    private fun getTrackingData(){
        viewModelScope.launch {
            getLocationTrackingDataUseCase().let { list ->
                _locationTrackingPointList.postValue(
                    list.map {
                        it.toTrackingPoint()
                    }
                )
            }
            synchronized(_callCount){
                _callCount.postValue(_callCount.value!!+1)
            }
        }
    }

    private fun getRelayPathData(){
        viewModelScope.launch {
            getRelayPathDataUseCase().let { list ->
                list.map {
                    it.toRelayPath()
                }
            }
            synchronized(_callCount){
                _callCount.postValue(_callCount.value!!+1)
            }
        }
    }

    private fun getRelayInfo(){
        viewModelScope.launch{
            getRelayInfoUseCase().let {
                _relayInfo.postValue(it.toRelayInfo())
            }
            synchronized(_callCount){
                _callCount.postValue(_callCount.value!!+1)
            }
        }
    }
}