package com.gdd.presentation.relay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.databinding.FragmentLoadRelayBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoadRelayFragment : BaseFragment<FragmentLoadRelayBinding>(
    FragmentLoadRelayBinding::bind, R.layout.fragment_load_relay
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: LoadRelayViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        if(!PermissionHelper.checkPermission(mainActivity,Manifest.permission.ACCESS_FINE_LOCATION)){
            PermissionHelper.requestPermission_fragment(
                this ,
                Manifest.permission.ACCESS_FINE_LOCATION,
                deniedListener = locationPermissionDeniedListener
            )
        }

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(onMapReadyCallBack)
        setFabSpeedDialUi()
    }
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @SuppressLint("MissingPermission")
    val onMapReadyCallBack = OnMapReadyCallback { map ->
//        val locationManager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
//        val curCoordinate = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
//        map.locationOverlay.apply {
//            this.isVisible = true
//            circleRadius = 100
//        }
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(curCoordinate.latitude, curCoordinate.longitude))
//            .animate(CameraAnimation.Easing, 2000)
//            .finishCallback {
//                showSnackBar("완료")
//            }
//            .cancelCallback {
//                showSnackBar("취소")
//            }
//
//        map.moveCamera(cameraUpdate)
        naverMap = map
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.uiSettings.isLocationButtonEnabled = true
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

    // region fab 코드 영역
    private fun setFabSpeedDialUi() {
        binding.fabCreateRelay.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fab_create_path, R.drawable.ic_path
            )
                .setLabel(getString(R.string.load_relay_create_path))
                .create()
        )
        binding.fabCreateRelay.addActionItem(
            SpeedDialActionItem.Builder(
                R.id.fab_create_distance, R.drawable.ic_distance
            )
                .setLabel(getString(R.string.load_relay_create_distance))
                .create()
        )

        binding.fabCreateRelay.setOnActionSelectedListener { item ->
            when (item.id) {
                R.id.fab_create_path -> {
//                    showJoinGroupDialog()
                    showSnackBar("path!!")
                }
                R.id.fab_create_distance -> {
                    showSnackBar("distance!!")
                }
            }
            binding.fabCreateRelay.close()
            return@setOnActionSelectedListener true
        }
    }

    // endregion

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}