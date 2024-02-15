package com.gdd.presentation.report

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.LoadingDialog
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.base.location.LocationProviderController
import com.gdd.presentation.databinding.FragmentReportBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_COLLAPSED
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

private const val TAG = "ReportFragment_Genseong"

@AndroidEntryPoint
class ReportFragment : BaseFragment<FragmentReportBinding>(
    FragmentReportBinding::bind, R.layout.fragment_report
) {
    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by viewModels()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var naverMap: NaverMap
    private lateinit var locationProviderController: LocationProviderController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        // 뒤로가기 버튼 제어
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "registerListener: ${bottomSheetBehavior.state}")
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        setDefaultUI()
                    } else {
                        parentFragmentManager.popBackStack()
                    }
                }
            })

        try {
            binding.tvTotalPoint.text = mainViewModel.user.totalCoin.toString() + " P"
        } catch (e: Exception){
            binding.tvTotalPoint.text = "- P"
        }



        //위치권한 확인
        checkLocationPermission()
        /**
         * 이 이후로 위치 권한이 필요한 코드를 작성
         */

        registerListener()
        registerObserve()
    }

    @SuppressLint("ClickableViewAccessibility", "MissingPermission")
    private fun registerListener() {
        binding.layoutBottomSheet.setOnTouchListener { _, _ ->
            true
        }

        binding.btnReport.setOnClickListener {
            setReportUI()
        }

        binding.fabCurLocation.setOnClickListener {
            binding.fabCurLocation.isEnabled = false
            locationProviderController.getCurrnetLocation {task ->
                if (!task.isCanceled){
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
                    mainActivity.dismissLoadingView()
                }
            }
        }

        binding.efabReportLocation.setOnClickListener {
            showReportDialog(naverMap.cameraPosition.target)
        }
    }

    private fun registerObserve() {
        reportViewModel.registReportResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess){
                showSnackBar("제보에 성공 했습니다\n우리 동네를 아껴주셔서 감사합니다!")
            } else {
                showSnackBar(result.exceptionOrNull()?.message ?: "")
            }
        }
    }

    private fun showReportDialog(latLng: LatLng){
        MaterialAlertDialogBuilder(mainActivity)
            .setTitle(mainActivity.getString(R.string.report_report))
            .setMessage("해당 위치를 제보하시겠습니까?")
            .setPositiveButton("확인"){ _,_ ->
                reportViewModel.registReport(
                    latLng.latitude,
                    latLng.longitude
                )
            }
            .setNegativeButton("취소"){ _,_ -> }
            .show()
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

        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.layout_map, it).commit()
            }
        mapFragment.getMapAsync(mapReadyCallback)
        mainActivity.showLoadingView()// 로딩 다이얼로그
    }

    private val locationPermissionDeniedListener: () -> Unit = {
        MaterialAlertDialogBuilder(_activity)
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

    @SuppressLint("MissingPermission")
    private val mapReadyCallback = OnMapReadyCallback { map ->
        naverMap = map
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap.uiSettings.apply {
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }
        binding.fabCurLocation.performClick()
        // Map 이외 초기 UI 세팅
        bottomSheetSetting()
        setDefaultUI()
    }

    private fun bottomSheetSetting() {
        // 바텀시트 제어
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    STATE_COLLAPSED -> {
                        naverMap.setContentPadding(0, 0, 0, 0)
                    }

                    STATE_EXPANDED -> {
                        naverMap.setContentPadding(0, 0, 0, binding.layoutBottomSheet.height)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

    }

    private fun setDefaultUI() {
        bottomSheetBehavior.state = STATE_EXPANDED
        binding.ivReportTargetMarker.visibility = View.GONE
    }

    private fun setReportUI() {
        bottomSheetBehavior.state = STATE_COLLAPSED
        binding.ivReportTargetMarker.visibility = View.VISIBLE
    }
}