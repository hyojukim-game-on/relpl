package com.gdd.presentation.relay.relaying

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.Point
import com.gdd.domain.usecase.relay.GetRelayInfoUseCase
import com.gdd.domain.usecase.relay.StopRelayUseCase
import com.gdd.domain.usecase.relay.tracking.ClearRelayPathDataUseCase
import com.gdd.domain.usecase.relay.tracking.ClearTrackingDataUseCase
import com.gdd.domain.usecase.relay.tracking.GetLocationTrackingDataUseCase
import com.gdd.domain.usecase.relay.tracking.GetRelayPathDataUseCase
import com.gdd.presentation.Event
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.mapper.DateFormatter
import com.gdd.presentation.model.RelayInfo
import com.gdd.presentation.model.RelayPath
import com.gdd.presentation.model.TrackingPoint
import com.gdd.presentation.model.mapper.toRelayInfo
import com.gdd.presentation.model.mapper.toRelayPath
import com.gdd.presentation.model.mapper.toTrackingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RelayStopInfoViewModel @Inject constructor(
    private val getLocationTrackingDataUseCase: GetLocationTrackingDataUseCase,
    private val getRelayPathDataUseCase: GetRelayPathDataUseCase,
    private val getRelayInfoUseCase: GetRelayInfoUseCase,
    private val stopRelayUseCase: StopRelayUseCase,
    private val clearRelayPathDataUseCase: ClearRelayPathDataUseCase,
    private val clearTrackingDataUseCase: ClearTrackingDataUseCase,
    private val prefManager: PrefManager
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

    private var _stopRelayResult = MutableLiveData<Result<Boolean>>()
    val stopRelayResult: LiveData<Result<Boolean>>
        get() = _stopRelayResult

    private var _clearRelayingDataResult = MutableLiveData<Event<Unit>>()
    val clearRelayingDataResult: LiveData<Event<Unit>>
        get() = _clearRelayingDataResult

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
        }
    }

    private fun getRelayPathData(){
        viewModelScope.launch {
            getRelayPathDataUseCase().let { list ->
                _relayPathList.postValue(
                    list.map {
                        it.toRelayPath()
                    }
                )
            }
        }
    }

    private fun getRelayInfo(){
        viewModelScope.launch{
            getRelayInfoUseCase().let {
                _relayInfo.postValue(it.toRelayInfo())
            }
        }
    }

    fun stopRelay(userId: Long, userNickname: String,memo: String,photoFile: File){
        viewModelScope.launch {
            stopRelayUseCase(
                userId,
                _relayInfo.value!!.id.toInt(),
                userNickname,
                _relayInfo.value!!.name,
                DateFormatter.millisToLongFormat(_locationTrackingPointList.value!!.first().timeMillis),
                DateFormatter.millisToLongFormat(_locationTrackingPointList.value!!.last().timeMillis),
                getMoveDistance(),
                getElapsedTime(),
                getMovePath(),
                memo,
                getMovePointCount(),
                photoFile
            ).let {
                _stopRelayResult.postValue(it)
            }
        }
    }

    private fun getMoveDistance(): Int{
        return if (prefManager.getRelayingMode() == PrefManager.RELAYING_MODE.PATH){
            val list = _relayPathList.value!!
            val myVisitedList = list.filter { it.myVisit }.toMutableList().apply {
                add(0,list.filter { it.beforeVisit }.last())
            }
            myVisitedList.zipWithNext().sumOf {
                it.first.latLng.distanceTo(it.second.latLng)
            }.toInt()
        } else {
            _locationTrackingPointList.value!!.zipWithNext().sumOf {
                it.first.latLng.distanceTo(it.second.latLng)
            }.toInt()
        }
    }

    private fun getElapsedTime(): Int{
        return ((_locationTrackingPointList.value!!.last().timeMillis - _locationTrackingPointList.value!!.first().timeMillis)/60000).toInt()
    }

    private fun getMovePath(): List<Point>{
        return if (prefManager.getRelayingMode() == PrefManager.RELAYING_MODE.PATH){
            val list = _relayPathList.value!!
            list.filter { it.myVisit }.toMutableList().apply {
                add(0,list.filter { it.beforeVisit }.last())
            }.map {
                Point(it.latLng.longitude,it.latLng.latitude)
            }
        } else {
            _locationTrackingPointList.value!!.map {
                Point(it.latLng.longitude,it.latLng.latitude)
            }
        }
    }

    private fun getMovePointCount(): Int{
        return if(prefManager.getRelayingMode() == PrefManager.RELAYING_MODE.PATH){
            _relayPathList.value!!.count { it.myVisit || it.beforeVisit }
        } else {
            0
        }
    }

    fun clearRelayingData(){
        viewModelScope.launch {
            prefManager.setRelayingMode(PrefManager.RELAYING_MODE.NONE)
            clearTrackingDataUseCase()
            clearRelayPathDataUseCase()
            _clearRelayingDataResult.postValue(Event(Unit))
        }
    }
}