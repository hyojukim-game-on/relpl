package com.gdd.presentation.relay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.domain.usecase.relay.CreatePathRelayUseCase
import com.gdd.domain.usecase.relay.GetPathRelayInfoUseCase
import com.gdd.domain.usecase.relay.JoinRelayUseCase
import com.gdd.domain.usecase.relay.RecommendPathUseCase
import com.gdd.domain.usecase.relay.SaveRelayInfoUseCase
import com.gdd.domain.usecase.relay.tracking.SaveRelayPathDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePathRelayViewModel @Inject constructor(
    private val recommendPathUseCase: RecommendPathUseCase,
    private val createPathRelayUseCase: CreatePathRelayUseCase,
    private val joinRelayUseCase: JoinRelayUseCase,
    private val getPathRelayInfoUseCase: GetPathRelayInfoUseCase,
    private val saveRelayInfoToLocalUseCase: SaveRelayInfoUseCase,
    private val saveRelayPathDataUseCase: SaveRelayPathDataUseCase
): ViewModel() {

    private val _recommendedPathResult = MutableLiveData<Result<RecommendedPath>>()
    val recommendedPathResult: LiveData<Result<RecommendedPath>>
        get() = _recommendedPathResult

    private val _createPathRelayResult = MutableLiveData<Result<Long>>()
    val createPathRelayResult: LiveData<Result<Long>>
        get() = _createPathRelayResult

    private val _joinRelayResult = MutableLiveData<Result<Long>>()
    val joinRelayResult: LiveData<Result<Long>>
        get() = _joinRelayResult

    private val _pathRelayInfoResult = MutableLiveData<Result<PathRelayInfo>>()
    val pathRelayInfoResult: LiveData<Result<PathRelayInfo>>
        get() = _pathRelayInfoResult

    var isRecommendedPathSelected = true
    var selectedPathId = ""
    var selectedPathDistance = 0
    lateinit var startCoordinate: Point
    lateinit var endCoordinate: Point
    var projectSelectedCoordinateTotalSize = 0

    fun recommendPath(startCoordinate: Point, endCoordinate: Point){
        viewModelScope.launch {

            recommendPathUseCase(startCoordinate, endCoordinate).let {
                _recommendedPathResult.postValue(it)
            }
        }
    }

    fun createPathRelay(userId: Long,
                        projectSelectedId : String,
                        projectSelectedTotalDistance: Int,
                        projectSelectedCoordinateTotalSize: Int,
                        projectName: String,
                        projectCreateDate: String,
                        projectEndDate: String,
                        projectStartPoint: Point,
                        projectEndPoint: Point
    ){
        viewModelScope.launch {
            createPathRelayUseCase(
                userId,
                projectSelectedId,
                projectSelectedTotalDistance,
                projectSelectedCoordinateTotalSize,
                projectName,
                projectCreateDate,
                projectEndDate,
                projectStartPoint,
                projectEndPoint
            ).let {
                _createPathRelayResult.postValue(it)
            }
        }
    }

    fun joinRelay(projectId: Long){
        viewModelScope.launch {
            joinRelayUseCase(projectId).let {
                _joinRelayResult.postValue(it)
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
}