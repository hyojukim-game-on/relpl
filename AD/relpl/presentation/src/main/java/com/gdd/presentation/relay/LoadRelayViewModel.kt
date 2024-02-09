package com.gdd.presentation.relay

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.IsExistDistanceRelay
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.relay.RelayMarker
import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.usecase.relay.CreateDistanceRelayUseCase
import com.gdd.domain.usecase.relay.GetAllRelayMarkerUseCase
import com.gdd.domain.usecase.relay.GetDistanceRelayInfoUseCase
import com.gdd.domain.usecase.relay.GetPathRelayInfoUseCase
import com.gdd.domain.usecase.relay.IsExistDistanceRelayUseCase
import com.gdd.domain.usecase.relay.JoinRelayUseCase
import com.gdd.domain.usecase.relay.SaveRelayInfoUseCase
import com.gdd.domain.usecase.relay.tracking.SaveRelayPathDataUseCase
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "LoadRelayViewModel_Genseong"
@HiltViewModel
class LoadRelayViewModel @Inject constructor(
    private val getAllRelayMarkerUseCase: GetAllRelayMarkerUseCase,
    private val getDistanceRelayInfoUseCase: GetDistanceRelayInfoUseCase,
    private val getPathRelayInfoUseCase: GetPathRelayInfoUseCase,
    private val isExistDistanceRelayUseCase: IsExistDistanceRelayUseCase,
    private val joinRelayUseCase: JoinRelayUseCase,
    private val createDistanceRelayUseCase: CreateDistanceRelayUseCase,
    private val saveRelayInfoToLocalUseCase: SaveRelayInfoUseCase,
    private val saveRelayPathDataUseCase: SaveRelayPathDataUseCase
) : ViewModel() {

    private val _markerResult = MutableLiveData<Result<List<RelayMarker>>>()
    val markerResult: LiveData<Result<List<RelayMarker>>>
        get() = _markerResult


    private val _isExistDistanceResult = MutableLiveData<Result<IsExistDistanceRelay>>()
    val isExistDistanceResult: LiveData<Result<IsExistDistanceRelay>>
        get() = _isExistDistanceResult

    private val _distanceRelayInfoResult = MutableLiveData<Result<DistanceRelayInfo>>()
    val distanceRelayInfoResult: LiveData<Result<DistanceRelayInfo>>
        get() = _distanceRelayInfoResult

    private val _pathRelayInfoResult = MutableLiveData<Result<PathRelayInfo>>()
    val pathRelayInfoResult: LiveData<Result<PathRelayInfo>>
        get() = _pathRelayInfoResult

    private val _joinRelayResult = MutableLiveData<Result<Long>>()
    val joinRelayResult : LiveData<Result<Long>>
        get() = _joinRelayResult


    private val _createDistanceRelayResult = MutableLiveData<Result<Long>>()
    val createDistanceRelayResult: LiveData<Result<Long>>
        get() = _createDistanceRelayResult

    fun getAllMarker(){
        viewModelScope.launch {
            getAllRelayMarkerUseCase().let {
                Log.d(TAG, "getAllMarker: ${it.getOrNull()?.size}")
                _markerResult.postValue(it)
            }
        }
    }

    fun isExistDistanceRelay(lat: Double, lng: Double){
        viewModelScope.launch {
            isExistDistanceRelayUseCase(lat, lng).let {
                _isExistDistanceResult.postValue(it)
            }
        }
    }

    fun getDistanceRelayInfo(projectId: Long){
        viewModelScope.launch {
            getDistanceRelayInfoUseCase(projectId).let {
                _distanceRelayInfoResult.postValue(it)
            }
        }
    }

    fun getPathRelayInfo(projectId: Long){
        viewModelScope.launch {
            getPathRelayInfoUseCase(projectId).let {
                _pathRelayInfoResult.postValue(it)
            }
        }
    }

    fun joinRelay(projectId: Long){
        viewModelScope.launch {
            joinRelayUseCase(projectId)?.let {
                _joinRelayResult.postValue(it)
            }
        }
    }

    fun createDistanceRelay(
        userId: Long,
        projectName: String,
        projectCreateDate: String,
        projectEndDate: String,
        projectTotalDistance: Int,
        projectStartCoordinate: Point
    ){
        viewModelScope.launch {
            createDistanceRelayUseCase(
                userId,
                projectName,
                projectCreateDate,
                projectEndDate,
                projectTotalDistance,
                projectStartCoordinate
            ).let {
                _createDistanceRelayResult.postValue(it)
            }
        }
    }

    suspend fun saveRelayInfoToLocal(
        id: Long,
        name: String,
        totalContributer: Int,
        totalDistance: Int,
        remainDistance: Int,
        createDate: String,
        endDate: String,
        isPath: Boolean,
        endLatitude: Double,
        endLongitude: Double
    ) {
        saveRelayInfoToLocalUseCase(
            id,
            name,
            totalContributer,
            totalDistance,
            remainDistance,
            createDate,
            endDate,
            isPath,
            endLatitude,
            endLongitude
        )
    }

    suspend fun saveRelayPathData(list: List<RelayPathData>){
        saveRelayPathDataUseCase(list)
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
            2230,
            1010,
            "2024년 1월 31일",
            "2024년 2월 5일",
            false,
            Point(36.108540, 128.420353),
            47,
            "동해물과 백두산이 마르고 닳도록 하느님이 보우하사 우리나라 만세"
        )
    }
}