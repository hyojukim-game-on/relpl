package com.gdd.presentation.relay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.relay.ClearTrackingDataUseCase
import com.gdd.domain.usecase.relay.GetLocationTrackingDataUseCase
import com.gdd.presentation.model.mapper.toTrackingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelayingViewModel @Inject constructor(
    private val getLocationTrackingDataUseCase: GetLocationTrackingDataUseCase,
    private val clearTrackingDataUseCase: ClearTrackingDataUseCase
): ViewModel() {
    val trackingStateFlow = getLocationTrackingDataUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf()
    ).map { list ->
        list.map {
            it.toTrackingPoint()
        }
    }

    fun stopTracking(){
        viewModelScope.launch {
            clearTrackingDataUseCase()
        }
    }
}