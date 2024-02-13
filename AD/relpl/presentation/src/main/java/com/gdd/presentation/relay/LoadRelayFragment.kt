package com.gdd.presentation.relay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.base.location.LocationProviderController
import com.gdd.presentation.base.splitWhen
import com.gdd.presentation.base.toLatLng
import com.gdd.presentation.databinding.FragmentLoadRelayBinding
import com.gdd.presentation.mapper.DateFormatter
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.leinardi.android.speeddial.SpeedDialActionItem
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
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val TAG = "LoadRelayFragment_Genseong"

@AndroidEntryPoint
class LoadRelayFragment : BaseFragment<FragmentLoadRelayBinding>(
    FragmentLoadRelayBinding::bind, R.layout.fragment_load_relay
), DialogClickInterface {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: LoadRelayViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var locationProviderController: LocationProviderController

    private val passedPath = PathOverlay()
    private val remainPath = PathOverlay()
    private val destinationMarker = Marker()

    @Inject
    lateinit var prefManager: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        val bottomSheetView = layoutInflater.inflate(R.layout.btm_sheet_distance_relay_expand, null)
        bottomSheetDialog = BottomSheetDialog(mainActivity)
        bottomSheetDialog.setContentView(bottomSheetView)

        //위치권한 확인
        checkLocationPermission()

        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bottomSheetDialog.isShowing) {
                        bottomSheetDialog.dismiss()
                    } else {
                        if (remainPath.map != null || passedPath.map != null) {
                            removeAllOverlay()
                        } else {
                            parentFragmentManager.popBackStack()
                        }
                    }
                }
            })

        registerListener()
        registerObserver()
        setFabSpeedDialUi()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        removeAllOverlay()
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

    @SuppressLint("MissingPermission")
    val mapReadyCallback = OnMapReadyCallback { map ->
        naverMap = map
        naverMap.locationTrackingMode = LocationTrackingMode.NoFollow
        naverMap.uiSettings.apply {
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
        }
        binding.fabCurLocation.performClick()

        viewModel.getAllMarker()
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

    private fun registerObserver() {
        viewModel.markerResult.observe(viewLifecycleOwner) { result ->
            Log.d(TAG, "registerObserver: ${result.getOrNull()?.size}")
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    it.forEach {
                        Log.d(TAG, "registerObserver: ${it.stopCoordinate}")
                        Marker().apply {
                            position = it.stopCoordinate.toLatLng()
                            map = naverMap
                            icon = OverlayImage.fromResource(R.drawable.ic_marker)
                            iconTintColor =
                                if (it.isPath) resources.getColor(R.color.sage_green_dark) else resources.getColor(
                                    R.color.sage_brown
                                )
                            tag = it.projectId.toString()
                            setOnClickListener { marker ->
                                if (it.isPath)
                                    viewModel.getPathRelayInfo(this.tag.toString().toLong())
                                else
                                    viewModel.getDistanceRelayInfo(this.tag.toString().toLong())
                                true
                            }
                        }
                    }
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
        }

        viewModel.isExistDistanceResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    //존재하므로 생성 막아야함
                    if (it.isExit) {
                        val latLng = it.projectCoordinate.toLatLng()
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
                        showCannotCreateDistanceProjectDialog()
                    } else {
                        // 거리 릴레이 생성 다이얼로그
                        val dialog = CreateDistanceRelayDialog(this)
                        dialog.isCancelable = false
                        dialog.show(this.childFragmentManager, "")
                    }
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
        }

        viewModel.distanceRelayInfoResult.observe(viewLifecycleOwner) { result ->
            removeAllOverlay()
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    initDistanceBottomSheetInfo(it)
                    naverMap.moveCamera(
                        CameraUpdate.scrollTo(it.stopCoordinate.toLatLng())
                            .animate(CameraAnimation.Easing)
                            .finishCallback {
                                naverMap.moveCamera(
                                    CameraUpdate.zoomTo(15.0)
                                        .animate(CameraAnimation.Easing)
                                )
                            }
                    )
                    bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    bottomSheetDialog.show()

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
        }

        viewModel.pathRelayInfoResult.observe(viewLifecycleOwner) { result ->
            removeAllOverlay()
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    initPathBottomSheetInfo(it)
                    naverMap.moveCamera(
                        CameraUpdate.scrollTo(it.stopCoordinate.toLatLng())
                            .animate(CameraAnimation.Easing)
                            .finishCallback {
                                naverMap.moveCamera(
                                    CameraUpdate.zoomTo(15.0)
                                        .animate(CameraAnimation.Easing)
                                )
                            }
                    )

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
        }

        /**
         * 여기입니다
         */
        viewModel.joinRelayResult.observe(viewLifecycleOwner) { result ->
            //참여 성공했으면 화면 전환
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    //여기서 화면 전환
                    showToast("$it 번 프로젝트 참여 성공")
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
        }

        viewModel.createDistanceRelayResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    viewModel.joinRelay(it)
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
        }
    }

    private fun removeAllOverlay() {
        passedPath.map = null
        remainPath.map = null
        destinationMarker.map = null
        bottomSheetDialog.dismiss()
    }

    private fun initDistanceBottomSheetInfo(data: DistanceRelayInfo) {
        bottomSheetDialog.findViewById<TextView>(R.id.tv_relay_name)?.text = data.projectName
        bottomSheetDialog.findViewById<TextView>(R.id.tv_people)?.text =
            data.totalContributor.toString()
        bottomSheetDialog.findViewById<LinearProgressIndicator>(R.id.pg_current)?.progress =
            data.progress
        bottomSheetDialog.findViewById<TextView>(R.id.tv_progress)?.text =
            "현재 ${data.progress}% 진행됐습니다"
        bottomSheetDialog.findViewById<TextView>(R.id.tv_total_distance)?.text = data.totalDistance
        bottomSheetDialog.findViewById<TextView>(R.id.tv_remain_distance)?.text =
            data.remainDistance
        bottomSheetDialog.findViewById<TextView>(R.id.tv_start_date)?.text = data.createDate
        bottomSheetDialog.findViewById<TextView>(R.id.tv_end_date)?.text = data.endDate
        bottomSheetDialog.findViewById<TextView>(R.id.tv_memo)?.text = data.memo

        bottomSheetDialog.findViewById<MaterialCardView>(R.id.btn_join_relay)?.setOnClickListener {
            locationProviderController.getCurrnetLocation { task ->
                if (!task.isCanceled) {
                    if (task.isSuccessful) {
                        task.result.also {
                            val cur = LatLng(it)
                            Log.d(TAG, "initDistanceBottomSheetInfo: ${data.stopCoordinate.toLatLng().distanceTo(cur)}")
                            if (data.stopCoordinate.toLatLng().distanceTo(cur) <= 5) {
                                viewModel.joinRelay(data.projectId)
                            }else{
                                showToast("릴레이 시작 지점과 5m 내에 위치해야 합니다(현재 ${((data.stopCoordinate.toLatLng().distanceTo(cur)*100).toInt()/100.0)}m)")
                            }
                        }
                    }else{
                        showSnackBar("위치정보 호출에 실패했습니다.")
                    }
                } else {
                    showSnackBar("위치정보 호출에 실패했습니다.")
                }
            }
        }
    }

    private fun initPathBottomSheetInfo(data: PathRelayInfo) {
        bottomSheetDialog.findViewById<TextView>(R.id.tv_relay_name)?.text = data.projectName
        bottomSheetDialog.findViewById<TextView>(R.id.tv_people)?.text =
            data.totalContributor.toString()
        bottomSheetDialog.findViewById<LinearProgressIndicator>(R.id.pg_current)?.progress =
            data.progress
        bottomSheetDialog.findViewById<TextView>(R.id.tv_progress)?.text =
            "현재 ${data.progress}% 진행됐습니다"
        bottomSheetDialog.findViewById<TextView>(R.id.tv_total_distance)?.text = data.totalDistance
        bottomSheetDialog.findViewById<TextView>(R.id.tv_remain_distance)?.text =
            data.remainDistance
        bottomSheetDialog.findViewById<TextView>(R.id.tv_start_date)?.text = data.createDate
        bottomSheetDialog.findViewById<TextView>(R.id.tv_end_date)?.text = data.endDate
        bottomSheetDialog.findViewById<TextView>(R.id.tv_memo)?.text = data.memo

        bottomSheetDialog.findViewById<MaterialCardView>(R.id.btn_join_relay)?.setOnClickListener {
            locationProviderController.getCurrnetLocation { task ->
                if (!task.isCanceled) {
                    if (task.isSuccessful) {
                        task.result.also {
                            val cur = LatLng(it)
                            if (data.stopCoordinate.toLatLng().distanceTo(cur) <= 5) {
                                viewModel.joinRelay(data.projectId)
                            }else{
                                showToast("릴레이 시작 지점과 5m 내에 위치해야 합니다(현재 ${((data.stopCoordinate.toLatLng().distanceTo(cur)*100).toInt()/100.0)}m)")
                            }
                        }
                    }else{
                        showSnackBar("위치정보 호출에 실패했습니다.")
                    }
                } else {
                    showSnackBar("위치정보 호출에 실패했습니다.")
                }
            }
        }

        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetDialog.show()

        drawPassedPath(data.route.map { it.toLatLng() }, data.stopCoordinate.toLatLng())
    }


    private fun drawPassedPath(path: List<LatLng>, point: LatLng) {
        val route = path.splitWhen {
            point.latitude == it.latitude && point.longitude == it.longitude
        }

        passedPath.apply {
            map = null
            coords = route[0]
            color = resources.getColor(R.color.sage_green)
            width = 20
            outlineWidth = 1
            map = naverMap
        }

        destinationMarker.apply {
            map = null
            position = route[0].last()
            icon = OverlayImage.fromResource(R.drawable.ic_marker)
            iconTintColor = resources.getColor(R.color.sage_orange)
            map = naverMap
        }

        if (route.size != 1) {
            remainPath.apply {
                map = null
                coords = route[1]
                color = resources.getColor(R.color.text_gray)
                width = 20
                outlineWidth = 1
                map = naverMap
            }
        }
    }

    private fun showCannotCreateDistanceProjectDialog() {
        MaterialAlertDialogBuilder(mainActivity)
            .setTitle("잠시만요!")
            .setMessage(
                "반경 50m 이내에 이어할 수 있는 거리 기반 릴레이가 존재합니다. \n" +
                        "릴레이를 이어 받아 완성해주세요!"
            )
            .setPositiveButton("확인") { _, _ -> }
            .show()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateButtonClick(name: String, distance: Int, endDate: String) {
        locationProviderController.getCurrnetLocation { task ->
            if (!task.isCanceled) {
                if (task.isSuccessful) {
                    task.result.also {
                        val latLng = LatLng(it)
                        viewModel.createDistanceRelay(
                            prefManager.getUserId(),
                            name,
                            DateFormatter.curMsToShorFormat(),
                            DateFormatter.koreanToShortFormat(endDate),
                            distance,
                            Point(it.longitude, it.latitude)
                        )
                        naverMap.locationOverlay.isVisible = true
                        naverMap.locationOverlay.position = latLng
                    }
                } else {
                    showSnackBar("위치정보 호출에 실패했습니다.")
                }
            }
        }
    }

    // region 권한 및 fab
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("MissingPermission")
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
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.layout_main_fragment, CreatePathRelayFragment())
                        .addToBackStack(null)
                        .commit()
                }

                R.id.fab_create_distance -> {
                    locationProviderController.getCurrnetLocation { task ->
                        if (!task.isCanceled) {
                            if (task.isSuccessful) {
                                task.result.also {
                                    val latLng = LatLng(it)
                                    viewModel.isExistDistanceRelay(it.latitude, it.longitude)
                                    naverMap.locationOverlay.isVisible = true
                                    naverMap.locationOverlay.position = latLng
                                }
                            } else {
                                showSnackBar("위치정보 호출에 실패했습니다.")
                            }
                        }
                    }
                }
            }
            binding.fabCreateRelay.close()
            return@setOnActionSelectedListener true
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

// endregion
}