package com.gdd.presentation.relay.relaying

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.relay.tracking.ClearTrackingDataUseCase
import com.gdd.domain.usecase.relay.tracking.GetLocationTrackingDataUseCaseFlow
import com.gdd.presentation.model.mapper.toTrackingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RelayingViewModel_Genseong"

@HiltViewModel
class RelayingViewModel @Inject constructor(
    private val getLocationTrackingDataUseCaseFlow: GetLocationTrackingDataUseCaseFlow,
    private val clearTrackingDataUseCase: ClearTrackingDataUseCase
) : ViewModel() {
    private var firstTime: Long? = null
    private var elapsedTimeFlag = true

    val trackingStateFlow = getLocationTrackingDataUseCaseFlow()
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            1
        )
        .map { list ->
            Log.d(TAG, "Viewmodel: trackingStateFlow")
            list.map {
                it.toTrackingPoint()
            }
        }

    fun stopTracking() {
        viewModelScope.launch {
            clearTrackingDataUseCase()
            viewModelScope.cancel()
        }
    }
}