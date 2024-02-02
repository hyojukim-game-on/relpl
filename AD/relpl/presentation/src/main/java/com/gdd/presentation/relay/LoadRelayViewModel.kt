package com.gdd.presentation.relay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.usecase.relay.GetAllRelayMarkerUseCase
import com.gdd.domain.usecase.relay.GetDistanceRelayInfoUseCase
import com.gdd.domain.usecase.relay.IsExistDistanceRelayUseCase
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoadRelayViewModel @Inject constructor(
    private val getAllRelayMarkerUseCase: GetAllRelayMarkerUseCase,
    private val getDistanceRelayInfoUseCase: GetDistanceRelayInfoUseCase,
    private val isExistDistanceRelayUseCase: IsExistDistanceRelayUseCase
) : ViewModel() {

    private val _createDistanceRelayDist = MutableLiveData<Int>()
    val createDistanceRelayDist : LiveData<Int>
        get() = _createDistanceRelayDist

    private val _markerResult = MutableLiveData<Result<List<RelayMarker>>>()
    val markerResult: LiveData<Result<List<RelayMarker>>>
        get() = _markerResult

    /*
    private val _markerResult = MutableLiveData<List<RelayMarker>>()
    val markerResult: LiveData<List<RelayMarker>>
        get() = _markerResult
     */

    private val _isExistDistanceResult = MutableLiveData<Result<Boolean>>()
    val isExistDistanceResult: LiveData<Result<Boolean>>
        get() = _isExistDistanceResult

    private val _distanceRelayInfoResult = MutableLiveData<Result<DistanceRelayInfo>>()
    val distanceRelayInfoResult: LiveData<Result<DistanceRelayInfo>>
        get() = _distanceRelayInfoResult

    fun getAllMarker(){
        viewModelScope.launch {
            delay(300)
            _markerResult.postValue(Result.success(tempMarkerList))
            getAllRelayMarkerUseCase().let {
//                _markerResult.postValue(it)
            }
        }
    }

    fun isExistDistanceRelay(lat: Double, lng: Double){
        viewModelScope.launch {
            _isExistDistanceResult.postValue(Result.success(System.currentTimeMillis()%5 == 0L))
//            isExistDistanceRelayUseCase(lat, lng).let {
//                _isExistDistanceResult.postValue(it)
//            }
        }
    }

    fun getDistanceRelayInfo(projectId: Long){
        viewModelScope.launch {
            delay(200)
            _distanceRelayInfoResult.postValue(Result.success(distanceRelayInfoEx))
//            getDistanceRelayInfoUseCase(projectId).let {
//                _distanceRelayInfoResult.postValue(it)
//            }
        }
    }

    fun setInitialDist(){
        _createDistanceRelayDist.value = 1000
    }

    fun plusMeterDist(){
        _createDistanceRelayDist.value!!.plus(100)
    }

    fun minusMeterDist(){
        if (_createDistanceRelayDist.value!! > 1000)
            _createDistanceRelayDist.value!!.minus(100)
    }

    fun plusKmDist(){
        _createDistanceRelayDist.value!!.plus(1000)
    }

    fun minusKmDist(){
        if (_createDistanceRelayDist.value!! > 2000)
            _createDistanceRelayDist.value!!.minus(1000)
    }


    companion object{
        val tempMarkerList = arrayListOf<RelayMarker>(
            RelayMarker(1, Point(36.108899, 128.418671), true),
            RelayMarker(2, Point(36.108540, 128.420353), true),
            RelayMarker(3, Point(36.107000, 128.422437), false),
            RelayMarker(4, Point(36.106996,  128.422379), true),
            RelayMarker(5, Point(36.105022, 128.422064), true),
            RelayMarker(6, Point(36.104238, 128.419391), true),
            RelayMarker(7, Point(36.104024, 128.418710), true),
            RelayMarker(8, Point(36.101772, 128.420475), false),
            RelayMarker(9, Point(36.101640, 128.421811), false),
            RelayMarker(10, Point(36.100745, 128.420818), true),
            RelayMarker(11, Point(36.104214, 128.425846), false),
        )

        val distanceRelayInfoEx = DistanceRelayInfo(
            2,
            "진평동 플로깅 합시다 제발~",
            3,
            "2km 230m",
            "1km 10m",
            "2024년 1월 31일",
            "2024년 2월 5일",
            false,
            Point(36.108540, 128.420353),
            "47%",
            "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세"
        )
    }
}