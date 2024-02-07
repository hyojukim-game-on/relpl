package com.gdd.presentation.relay.relaying

import com.gdd.domain.usecase.relay.tracking.GetLocationTrackingDataUseCase
import com.gdd.domain.usecase.relay.tracking.GetRelayPathDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RelayStopInfoViewModel @Inject constructor(
    private val getLocationTrackingDataUseCase: GetLocationTrackingDataUseCase,
    private val getRelayPathDataUseCase: GetRelayPathDataUseCase
) {

}