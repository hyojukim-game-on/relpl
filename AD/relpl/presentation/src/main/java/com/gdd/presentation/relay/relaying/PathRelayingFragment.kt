package com.gdd.presentation.relay.relaying

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gdd.presentation.R
import com.gdd.presentation.base.location.relaying_service.DistanceLocationTrackingService
import com.gdd.presentation.base.location.relaying_service.PathLocationTrackingService
import com.gdd.presentation.mapper.DateFormatter
import com.gdd.presentation.model.mapper.toStringDistance
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "PathRelayingFragment_Genseong"
@AndroidEntryPoint
class PathRelayingFragment : RelayingFragment() {
    private val pathRelayingViewModel: PathRelayingViewModel by viewModels()

    override var naverMap: NaverMap? = null
    private var beforePathOverlay = PathOverlay()
    private var myPathOverlay = PathOverlay()
    private var afterPathOverlay = PathOverlay()

    private var firstTime: Long? = null
    private var elapsedTimeFlag = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beforePathOverlay.color = _activity.getColor(R.color.sage_blue)
        myPathOverlay.color = _activity.getColor(R.color.sage_green)
        afterPathOverlay.color = _activity.getColor(R.color.divider_gray)
    }

    override fun registerListener() {
        binding.btnStopRelay.setOnClickListener {
            if (progressIsNotZero){
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
                    if (list.isNotEmpty()) {
                        naverMap?.let { map ->
                            map.locationOverlay.position = list.last().latLng
                            map.moveCamera(
                                CameraUpdate.scrollTo(list.last().latLng)
                                    .animate(CameraAnimation.Linear)
                            )
                        }
                    }
                    // 시작 시간
                    if (list.isNotEmpty() && binding.tvStartTime.text.isNullOrBlank()) {
                        firstTime = list.first().timeMillis

                        binding.tvStartTime.text =
                            DateFormatter.getRelayStartTimeString(list.first().timeMillis)
                    }
                }
            }
        }

        // relayPathFlow
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pathRelayingViewModel.relayPathStateFlow.collect { list ->
                    var totalDistance = list.zipWithNext().sumOf {
                        it.first.latLng.distanceTo(it.second.latLng)
                    }.toInt()
                    var myDistance = 0
                    var remainDistance = 0
                    // 경로선 그리기
                    val beforeVisitList = list.filter { it.beforeVisit }
                    val myVisitedList = list.filter { it.myVisit }.toMutableList().apply {
                        add(0,beforeVisitList.last())
                    }
                    val afterVisitList = list.filter { !it.beforeVisit && !it.myVisit }.toMutableList().apply {
                        add(0,myVisitedList.last())
                    }
                    if (beforeVisitList.size >= 2){
                        beforePathOverlay.coords = beforeVisitList.map { it.latLng }
                        beforePathOverlay.map = naverMap
                    }
                    if (myVisitedList.size >= 2){
                        myPathOverlay.coords = myVisitedList.map { it.latLng }
                        myPathOverlay.map = naverMap
                        myDistance = myVisitedList.zipWithNext().sumOf {
                            it.first.latLng.distanceTo(it.second.latLng)
                        }.toInt()
                    }
                    if (afterVisitList.size >= 2){
                        afterPathOverlay.coords = afterVisitList.map { it.latLng }
                        afterPathOverlay.map = naverMap
                        remainDistance = afterVisitList.zipWithNext().sumOf {
                            it.first.latLng.distanceTo(it.second.latLng)
                        }.toInt()
                    }
                    // 남은 거리
                    binding.tvRemainDistance.text = remainDistance.toStringDistance()
                    // 진행 거리
                    binding.tvProgressDistance.text = myDistance.toStringDistance()
                    // 진행률
                    Log.d(TAG, "progress: $totalDistance - $remainDistance / $totalDistance")
                    val progress = (((totalDistance-remainDistance)/totalDistance.toDouble())*100).toInt()
                    binding.pgCurrent.progress = progress
                    binding.tvProgress.text = "현재 ${progress}% 진행됐습니다."
                    if (!progressIsNotZero && myDistance>0){
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

    override fun startRelay() {
        _activity.startForegroundService(
            Intent(
                _activity,
                PathLocationTrackingService::class.java
            )
        )
    }

    override fun stopRelay() {
        _activity.stopService(Intent(_activity, PathLocationTrackingService::class.java))
        viewLifecycleOwner.lifecycleScope.cancel()
        relayingViewModel.stopTracking()
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