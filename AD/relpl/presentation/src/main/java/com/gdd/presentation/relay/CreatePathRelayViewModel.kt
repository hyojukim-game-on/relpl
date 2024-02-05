package com.gdd.presentation.relay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.RecommendedPath
import com.gdd.domain.usecase.relay.RecommendPathUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePathRelayViewModel @Inject constructor(
    private val recommendPathUseCase: RecommendPathUseCase
): ViewModel() {

    private val _recommendedPathResult = MutableLiveData<Result<RecommendedPath>>()
    val recommendedPathResult: LiveData<Result<RecommendedPath>>
        get() = _recommendedPathResult

    fun recommendPath(startCoordinate: Point, endCoordinate: Point){
        viewModelScope.launch {
            recommendPathUseCase(startCoordinate, endCoordinate).let {
                _recommendedPathResult.postValue(it)
            }
        }
    }

}