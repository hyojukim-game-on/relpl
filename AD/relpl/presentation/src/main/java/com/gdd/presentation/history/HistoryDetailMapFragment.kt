package com.gdd.presentation.history

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.base.location.LocationProviderController
import com.gdd.presentation.base.toLatLng
import com.gdd.presentation.databinding.FragmentHistoryDetailMapBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryDetailMapFragment : BaseFragment<FragmentHistoryDetailMapBinding>(
    FragmentHistoryDetailMapBinding::bind, R.layout.fragment_history_detail_map) {

    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    private lateinit var naverMap: NaverMap
    private lateinit var locationProviderController: LocationProviderController

    private lateinit var colorList: List<Int>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        locationProviderController = LocationProviderController(mainActivity, viewLifecycleOwner)
        //위치권한 확인
        checkLocationPermission()

        colorList = listOf<Int>(
            resources.getColor(R.color.sage_green),
            resources.getColor(R.color.sage_blue),
            resources.getColor(R.color.sage_orange),
            resources.getColor(R.color.sage_brown_dark)
        )

        registerListener()
    }

    private fun registerListener() {
        binding.fabCurLocation.setOnClickListener {
            binding.fabCurLocation.isEnabled = false
            locationProviderController.getCurrnetLocation { task ->
                if (!task.isCanceled) {
                    if (task.isSuccessful) {
                        task.result.also {
                            val latLng = LatLng(it)
                            naverMap.moveCamera(
                                CameraUpdate.scrollTo(latLng).animate(CameraAnimation.Easing)
                                    .finishCallback {
                                        naverMap.moveCamera(
                                            CameraUpdate.zoomTo(16.0)
                                                .animate(CameraAnimation.Easing)
                                        )
                                    }
                            )
                            naverMap.locationOverlay.isVisible = true
                            naverMap.locationOverlay.position = latLng
                        }
                    } else {
                        showSnackBar("위치정보 호출에 실패했습니다.")
                    }
                    binding.fabCurLocation.isEnabled = true
                }
            }
        }
    }

    private fun registerObserver(){
        activityViewModel.historyDetailResult.observe(viewLifecycleOwner){ result ->
            if (result != null){
                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        drawPath(it)
                    }
                } else {
                    result.exceptionOrNull()?.let {
                        if (it is RelplException) {
                            showSnackBar(it.message)
                        } else {
                            showSnackBar(resources.getString(R.string.all_net_err))
                        }
                    }
                }
            }else{
                showSnackBar("경로 정보를 불러오지 못했습니다 :(")
            }
        }
    }

    private fun drawPath(data: HistoryDetailInfo){
        data.detailList.forEachIndexed { index, historyDetail ->
            PathOverlay().apply {
                map = null
                coords = historyDetail.movePath.map { it.toLatLng() }
                color = colorList[index % colorList.size]
                width = 25
                outlineWidth = 1
                map = naverMap
            }
        }
        val firstPath = data.detailList.first()
        val lastPath = data.detailList.last()

        Marker().apply {
            position = firstPath.movePath.first().toLatLng()
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_marker)
            iconTintColor =  colorList[0]
            captionText = "시작 지점"
        }

        Marker().apply {
            position = lastPath.movePath.last().toLatLng()
            map = naverMap
            icon = OverlayImage.fromResource(R.drawable.ic_marker)
            iconTintColor =  colorList[data.detailList.size % colorList.size]
            captionText = "종료 지점"
        }
    }

    @SuppressLint("MissingPermission")
    val mapReadyCallback = OnMapReadyCallback { map ->
        naverMap = map
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap.uiSettings.apply {
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }

        naverMap.moveCamera(
            CameraUpdate.scrollTo(LatLng(36.106947, 128.421853)).animate(CameraAnimation.Easing)
                .finishCallback {
                    naverMap.moveCamera(
                        CameraUpdate.zoomTo(14.0)
                            .animate(CameraAnimation.Easing)
                    )
                }
        )

        registerObserver()
    }

    private fun checkLocationPermission() {
        if (!PermissionHelper.checkPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            PermissionHelper.requestPermission_fragment(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                grantedListener = locationPermissionGrantedListener,
                deniedListener = locationPermissionDeniedListener
            )
        } else {
            locationPermissionGrantedListener()
        }
    }

    private val locationPermissionGrantedListener: () -> Unit = {
        locationProviderController = LocationProviderController(mainActivity, viewLifecycleOwner)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(mapReadyCallback)
    }

    private val locationPermissionDeniedListener: () -> Unit = {
        MaterialAlertDialogBuilder(mainActivity)
            .setTitle("정확한 위치 권한 허용이 필요합니다")
            .setMessage("정확한 위치 권한을 허용하지 않을 경우 해당 기능을 이용할 수 없습니다. 설정으로 이동하시겠습니까?")
            .setNegativeButton("취소") { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .setPositiveButton("확인") { _, _ ->
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${mainActivity.packageName}")
                    ).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            .show()
    }
}