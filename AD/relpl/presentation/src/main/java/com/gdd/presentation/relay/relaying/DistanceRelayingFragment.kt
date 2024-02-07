package com.gdd.presentation.relay.relaying

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.gdd.presentation.R
import com.gdd.presentation.base.location.relaying_service.DistanceLocationTrackingService
import com.gdd.presentation.mapper.DateFormatter
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.CameraUpdateParams
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "DistanceRelayingFragmen_Genseong"

@AndroidEntryPoint
class DistanceRelayingFragment : RelayingFragment() {

    override var naverMap: NaverMap? = null
    private var pathOverlay = PathOverlay()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(pathOverlay) {
            color = _activity.getColor(R.color.sage_green)
        }
    }

    override fun registerListener() {
        binding.btnStopRelay.setOnClickListener {
            stopRelay()
        }
    }

    override fun registerObserve() {
        // trackingStateFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                relayingViewModel.trackingStateFlow.collect { list ->
                    Log.d(TAG, "registerObserve: trackingStateFlow")
                    Log.d(TAG, "registerObserve: $list")
                    if (list.isNotEmpty()) {
                        naverMap?.let { map ->
                            map.locationOverlay.position = list.last().latLng
                            map.moveCamera(
                                CameraUpdate.scrollTo(list.last().latLng)
                                    .animate(CameraAnimation.Linear)
                            )
                        }
                        if (list.size >= 2) {
                            pathOverlay.coords = list.map { it.latLng }
                            if (pathOverlay.map == null) {
                                pathOverlay.map = naverMap
                            }
                        }
                    }
                }
            }
        }
        registerUiObserve()
    }

    private var firstTime: Long? = null
    private var elapsedTimeFlag = true
    private fun registerUiObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                relayingViewModel.trackingStateFlow.collect { list ->
                    Log.d(TAG, "registerUiObserve: $list")
                    Log.d(TAG, "registerUiObserve: $firstTime")
                    // 시작 시간
                    if (list.isNotEmpty() && binding.tvStartTime.text.isNullOrBlank()) {
                        firstTime = list.first().timeMillis
                        
                        binding.tvStartTime.text =
                            DateFormatter.getRelayStartTimeString(list.first().timeMillis)
                    }
                    // 진행 거리
                    binding.tvProgressDistance.text = list.zipWithNext().sumOf {
                        it.first.latLng.distanceTo(it.second.latLng)
                    }.toInt().toString() + "m"
                }
            }
        }

        // 경과 시간
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            while (elapsedTimeFlag) {
                Log.d(TAG, "registerUiObserve: Main")
                if (firstTime != null) {
                    Log.d(TAG, "registerUiObserve: Main2")
                    val hour = (System.currentTimeMillis() - firstTime!!) / 3600000
                    val minute = (System.currentTimeMillis() - firstTime!!) % 3600000 / 60000
                    withContext(Dispatchers.Main){
                        binding.tvElapsedTime.text =
                            "우리 동네를 위한 ${if (hour < 10) "0" else ""}${hour}시간 ${if (minute < 10) "0" else ""}${minute}분"
                    }
                }
                delay(1000)
            }
        }
    }

    override fun startRelay() {
        _activity.startForegroundService(
            Intent(
                _activity,
                DistanceLocationTrackingService::class.java
            )
        )
    }

    override fun stopRelay() {
        _activity.stopService(Intent(_activity, DistanceLocationTrackingService::class.java))
        viewLifecycleOwner.lifecycleScope.cancel()
        relayingViewModel.stopTracking()
    }


    override val mapReady = OnMapReadyCallback { map ->
        naverMap = map
        naverMap!!.run {
            uiSettings.apply {
                isTiltGesturesEnabled = false
                isRotateGesturesEnabled = false
            }
            locationOverlay.isVisible = true
            moveCamera(CameraUpdate.zoomTo(18.0).animate(CameraAnimation.Easing))

            addOnCameraIdleListener {
                moveCamera(
                    CameraUpdate.scrollTo(locationOverlay.position).animate(CameraAnimation.Linear)
                )
            }
        }

        registerListener()
        registerObserve()
    }
}