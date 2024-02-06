package com.gdd.presentation.relay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.base.location.LocationTrackingService
import com.gdd.presentation.databinding.FragmentRelayingBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "RelayingFragment_Genseong"

@AndroidEntryPoint
class RelayingFragment : BaseFragment<FragmentRelayingBinding>(
    FragmentRelayingBinding::bind, R.layout.fragment_relaying
) {
    private val relayingViewModel: RelayingViewModel by viewModels()

    private var naverMap: NaverMap? = null
    private lateinit var path: PathOverlay


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        path = PathOverlay().apply {
            color = _activity.getColor(R.color.sage_green)
        }

        checkPermissions()
        registerListener()
        registerObserve()
    }

    private fun registerListener(){
        binding.fabStop.setOnClickListener {
            _activity.stopService(Intent(_activity,LocationTrackingService::class.java))
            viewLifecycleOwner.lifecycleScope.cancel()
            relayingViewModel.stopTracking()
        }
    }

    private fun registerObserve() {
        collectLatestStateFlow(relayingViewModel.trackingStateFlow) { list ->
            naverMap?.run {
                val point = list.last()
                moveCamera(CameraUpdate.scrollTo(point.latLng).animate(CameraAnimation.Linear))
                locationOverlay.position = point.latLng
            }
            if (list.size >= 2){
                path.coords = list.map { it.latLng }
                path.map = naverMap
            }
        }
    }

    @SuppressLint("MissingPermission")
    private val mapReadyCallback = OnMapReadyCallback { map ->
        naverMap = map
        naverMap!!.run {
            uiSettings.apply {
                isTiltGesturesEnabled = false
                isRotateGesturesEnabled = false
            }
            locationOverlay.isVisible = true
        }
    }

    private fun checkPermissions() {
        if (PermissionHelper.checkPermissionList(
                _activity,
                requestPermissions
            ).isNotEmpty()
        ) {
            PermissionHelper.requestPermissionList_fragment(
                this,
                requestPermissions.toTypedArray(),
                permissionGrantedListener,
                permissionDeniedListener
            )
        } else {
            permissionGrantedListener()
        }
    }


    private val permissionGrantedListener: () -> Unit = {
        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.layout_map, it).commit()
            }
        mapFragment.getMapAsync(mapReadyCallback)
        _activity.startForegroundService(Intent(_activity, LocationTrackingService::class.java)).apply {
            Log.d(TAG, "ComponentName: $this")
        }
    }

    private val permissionDeniedListener: () -> Unit = {
        MaterialAlertDialogBuilder(_activity)
            .setTitle("다음의 권한 허용이 필요합니다")
            .setMessage("- 알람 권한\n- 정확한 위치 권한\n설정으로 이동하시겠습니까?")
            .setNegativeButton("취소") { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .setPositiveButton("확인") { _, _ ->
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${_activity.packageName}")
                    ).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            .show()
    }

    private val requestPermissions =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
            listOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_FINE_LOCATION)
        else
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)
}

fun <T> Fragment.collectLatestStateFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}