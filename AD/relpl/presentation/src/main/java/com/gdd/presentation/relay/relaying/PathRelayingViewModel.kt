package com.gdd.presentation.relay.relaying

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.relay.tracking.GetRelayPathDataUseCaseFlow
import com.gdd.presentation.model.mapper.toRelayPath
import com.gdd.presentation.model.mapper.toTrackingPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class PathRelayingViewModel @Inject constructor(
    private val relayPathDataUseCaseFlow: GetRelayPathDataUseCaseFlow
): ViewModel() {
    val relayPathStateFlow = relayPathDataUseCaseFlow()
        .shareIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            1
        )
        .map { list ->
            list.map {
                it.toRelayPath()
            }
        }
}