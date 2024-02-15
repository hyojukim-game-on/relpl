package com.gdd.presentation.relay.relaying

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.databinding.FragmentRelayStopInfoBinding
import com.gdd.presentation.model.mapper.toStringDistance
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

private const val TAG = "RelayStopInfoFragment_Genseong"
@AndroidEntryPoint
class RelayStopInfoFragment : BaseFragment<FragmentRelayStopInfoBinding>(
    FragmentRelayStopInfoBinding::bind, R.layout.fragment_relay_stop_info
) {
    @Inject
    lateinit var prefManager: PrefManager

    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private val relayStopInfoViewModel: RelayStopInfoViewModel by activityViewModels()

    private lateinit var naverMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNext.isEnabled = false

        mainActivity = _activity as MainActivity
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showToast("릴레이 종료 등록을 마무리 해주세요!\n정상적인 등록이 되지 않을 수 있습니다.")
                }
            })

        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.layout_map, it).commit()
            }
        mapFragment.getMapAsync(mapCallBack)
    }

    private fun registerListener(){
        binding.btnNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.layout_main_fragment,RelayStopPicMenoFragment())
                .addToBackStack(null)
                .commit()
        }
    }


    private fun registerObserve(){
        relayStopInfoViewModel.relayInfo.observe(viewLifecycleOwner){
            Log.d(TAG, "registerObserve: relayInfo")
            setUI()
        }

        relayStopInfoViewModel.locationTrackingPointList.observe(viewLifecycleOwner){
            Log.d(TAG, "registerObserve: tracking")
            setUI()
        }

        relayStopInfoViewModel.relayPathList.observe(viewLifecycleOwner){
            Log.d(TAG, "registerObserve: path")
            setUI()
        }
    }

    private fun setUI(){
        when(prefManager.getRelayingMode()){
            PrefManager.RELAYING_MODE.PATH -> {
                if (
                    relayStopInfoViewModel.relayInfo.value != null
                    && !relayStopInfoViewModel.locationTrackingPointList.value.isNullOrEmpty()
                    && !relayStopInfoViewModel.relayPathList.value.isNullOrEmpty()
                ){
                    setUiPath()
                }
            }
            PrefManager.RELAYING_MODE.DISTANCE -> {
                if (
                    relayStopInfoViewModel.relayInfo.value != null
                    && !relayStopInfoViewModel.locationTrackingPointList.value.isNullOrEmpty()
                ){
                    setUiDistance()
                }
            }
            PrefManager.RELAYING_MODE.NONE -> {

            }
        }
        if (
            relayStopInfoViewModel.relayInfo.value != null
            && relayStopInfoViewModel.locationTrackingPointList.value != null
            && relayStopInfoViewModel.relayPathList.value != null
        ){
            Log.d(TAG, "setUI: not null")

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUiCommon(){
        kotlin.runCatching { binding.tvNickname.text = "${mainViewModel.user.nickname}님" }
        val dateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREA)
        val startMillis = relayStopInfoViewModel.locationTrackingPointList.value!!.first().timeMillis
        val endMillis = relayStopInfoViewModel.locationTrackingPointList.value!!.last().timeMillis
        binding.tvRelayName.text = relayStopInfoViewModel.relayInfo.value!!.name
        binding.tvStartTime.text = dateFormat.format(startMillis)
        binding.tvEndTime.text = dateFormat.format(endMillis)
        binding.tvElapsedTime.text = "${(endMillis-startMillis)/60000}분"
    }

    @SuppressLint("SetTextI18n")
    private fun setUiDistance(){
        setUiCommon()
        val pathOverlay = PathOverlay().apply {
            color = _activity.getColor(R.color.sage_green)
        }
        val moveList = relayStopInfoViewModel.locationTrackingPointList.value!!.map { it.latLng }
        if (moveList.size >= 2) {
            pathOverlay.coords = moveList
            pathOverlay.map = naverMap
        }
        binding.tvDistance.text =  relayStopInfoViewModel.locationTrackingPointList.value!!.zipWithNext().sumOf {
            it.first.latLng.distanceTo(it.second.latLng)
        }.toInt().toString() + "m"
        naverMap.moveCamera(CameraUpdate.fitBounds(
            LatLngBounds.from(moveList)
        ).animate(CameraAnimation.Easing))
        naverMap.addOnCameraIdleListener {
            naverMap.moveCamera(CameraUpdate.fitBounds(
                LatLngBounds.from(moveList)
            ).animate(CameraAnimation.Easing))
        }
        binding.btnNext.isEnabled = true
    }

    private fun setUiPath(){
        setUiCommon()
        // 경로선 그리기
        val list = relayStopInfoViewModel.relayPathList.value!!
        var beforePathOverlay = PathOverlay().apply { color = _activity.getColor(R.color.sage_blue) }
        var myPathOverlay = PathOverlay().apply { color = _activity.getColor(R.color.sage_green) }
        var afterPathOverlay = PathOverlay().apply { color = _activity.getColor(R.color.divider_gray) }
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
            binding.tvDistance.text = myVisitedList.zipWithNext().sumOf {
                it.first.latLng.distanceTo(it.second.latLng)
            }.toInt().toStringDistance()
        }
        if (afterVisitList.size >= 2){
            afterPathOverlay.coords = afterVisitList.map { it.latLng }
            afterPathOverlay.map = naverMap
        }
        naverMap.moveCamera(CameraUpdate.fitBounds(
            LatLngBounds.from(list.map { it.latLng })
        ).animate(CameraAnimation.Easing)
        )
        naverMap.addOnCameraIdleListener {
            naverMap.moveCamera(
                CameraUpdate.fitBounds(
                    LatLngBounds.from(list.map { it.latLng })
                ).animate(CameraAnimation.Easing)
            )
        }
        binding.btnNext.isEnabled = true
    }


    private val mapCallBack = OnMapReadyCallback{map ->
        naverMap = map
        val dp_20_px = dpToPx(20f)
        naverMap.setContentPadding(dp_20_px,dp_20_px,dp_20_px,dp_20_px)
        naverMap.uiSettings.apply {
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
            isZoomControlEnabled = false
        }
        registerObserve()
        registerListener()
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * _activity.resources.displayMetrics.density + 0.5f).toInt()
    }
}