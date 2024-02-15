package com.gdd.presentation.relay.relaying

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewModelScope
import com.gdd.presentation.R
import com.gdd.presentation.base.location.relaying_service.DistanceLocationTrackingService
import com.gdd.presentation.mapper.DateFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import kotlinx.coroutines.flow.first
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
            if (progressIsNotZero) {
                showStopDialog()
            } else {
                showToast("진행 거리가 0m 일때는 종료할 수 없습니다!")
            }
        }
    }

    override fun registerObserve() {
        // relay info
        relayingViewModel.relayInfo.observe(viewLifecycleOwner){
            binding.tvRelayName.text = it.name
            binding.tvPeople.text = (it.totalContributer+1).toString()
        }

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
    @SuppressLint("SetTextI18n")
    private fun registerUiObserve() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                relayingViewModel.trackingStateFlow.collect { list ->
                    // 시작 시간
                    if (list.isNotEmpty() && binding.tvStartTime.text.isNullOrBlank()) {
                        firstTime = list.first().timeMillis
                        
                        binding.tvStartTime.text =
                            DateFormatter.getRelayStartTimeString(list.first().timeMillis)
                    }
                    // 진행 거리
                    val progressDistance = list.zipWithNext().sumOf {
                        it.first.latLng.distanceTo(it.second.latLng)
                    }.toInt()
                    binding.tvProgressDistance.text = "${progressDistance}M"
                    binding.tvRemainDistance.text = relayingViewModel.relayInfo.value?.let {
                        "${it.remainDistance - progressDistance}M"
                    } ?: "측정중..."
                    //진행률
                    Log.d(TAG, "registerUiObserve: ${relayingViewModel.relayInfo.value}")
                    val progressPercent = relayingViewModel.relayInfo.value?.let {
                        (((it.totalDistance - it.remainDistance + progressDistance)/it.totalDistance.toDouble())*100.0).toInt()
                    } ?: 0
                    Log.d(TAG, "registerUiObserve: $progressPercent")
                    binding.pgCurrent.progress = progressPercent
                    binding.tvProgress.text = "현재 ${progressPercent}% 진행됐습니다."
                    if (!progressIsNotZero && progressDistance>0){
                        progressIsNotZero = true
                    }
                }
            }
        }

        // 경과 시간
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            while (elapsedTimeFlag) {
                if (firstTime != null) {
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

    private fun showStopDialog(){
        MaterialAlertDialogBuilder(_activity)
            .setTitle("릴레이 플로깅 그만하기")
            .setMessage("여기까지 할까요?")
            .setNegativeButton("취소") { _, _ ->

            }
            .setPositiveButton("확인") { _, _ ->
                stopRelay()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.layout_main_fragment,RelayStopInfoFragment())
                    .addToBackStack(null)
                    .commit()
            }
            .show()
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