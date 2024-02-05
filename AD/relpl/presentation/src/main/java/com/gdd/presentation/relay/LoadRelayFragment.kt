package com.gdd.presentation.relay

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.view.isNotEmpty
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.fragment.app.viewModels
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.base.toLatLng
import com.gdd.presentation.databinding.FragmentLoadRelayBinding
import com.gdd.presentation.mapper.DateFormatter
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputLayout
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import javax.inject.Inject


private const val TAG = "LoadRelayFragment_Genseong"
@AndroidEntryPoint
class LoadRelayFragment : BaseFragment<FragmentLoadRelayBinding>(
    FragmentLoadRelayBinding::bind, R.layout.fragment_load_relay
) , DialogClickInterface{
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: LoadRelayViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var locationManager: LocationManager

    @Inject
    lateinit var prefManager: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        val bottomSheetView = layoutInflater.inflate(R.layout.btm_sheet_distance_relay_expand, null)
        bottomSheetDialog = BottomSheetDialog(mainActivity)
        bottomSheetDialog.setContentView(bottomSheetView)

        locationManager = mainActivity.getSystemService(LOCATION_SERVICE) as LocationManager

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
        registerObserver()
        mapFragment.getMapAsync(onMapReadyCallBack)
        setFabSpeedDialUi()
    }

    @SuppressLint("MissingPermission")
    val onMapReadyCallBack = OnMapReadyCallback { map ->
        naverMap = map
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.uiSettings.isLocationButtonEnabled = true

        viewModel.getAllMarker()
    }

    private fun registerObserver(){
        viewModel.markerResult.observe(viewLifecycleOwner){ result ->
            Log.d(TAG, "registerObserver: ${result.getOrNull()?.size}")
            if (result.isSuccess){
                result.getOrNull()?.let {
                    it.forEach{
                        Log.d(TAG, "registerObserver: ${it.stopCoordinate}")
                        Marker().apply {
                            position =  it.stopCoordinate.toLatLng()
                            map = naverMap
                            icon = OverlayImage.fromResource(R.drawable.ic_marker)
                            iconTintColor = if (it.isPath) resources.getColor(R.color.sage_green_dark) else resources.getColor(R.color.sage_brown)
                            tag = it.projectId.toString()
                            setOnClickListener {marker ->
                                if (it.isPath)
                                    viewModel.getDistanceRelayInfo(this.tag.toString().toLong())
                                else
                                    viewModel.getDistanceRelayInfo(this.tag.toString().toLong())
                                true
                            }
                        }
                    }
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showSnackBar(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }

        viewModel.isExistDistanceResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                result.getOrNull()?.let {
                    //존재하므로 생성 막아야함
                    if (it){
                        showCannotCreateDistanceProjectDialog()
                    }else{
                        // 거리 릴레이 생성 다이얼로그
                        val dialog = CreateDistanceRelayDialog(this)
                        dialog.isCancelable = false
                        dialog.show(this.childFragmentManager, "")
                    }
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showSnackBar(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }

        viewModel.distanceRelayInfoResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                result.getOrNull()?.let {
                    initBottomSheetInfo(it)
                    bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    bottomSheetDialog.findViewById<TextView>(R.id.tv_relay_name)!!.text = it.projectName
                    bottomSheetDialog.show()

                }
            }
        }

        viewModel.joinRelayResult.observe(viewLifecycleOwner){ result ->
            //화면 전환
        }

        viewModel.createDistanceRelayResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                result.getOrNull()?.let {
                    viewModel.joinRelay(it)
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showSnackBar(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }
    }

    private fun initBottomSheetInfo(data: DistanceRelayInfo){
        bottomSheetDialog.findViewById<TextView>(R.id.tv_relay_name)?.text = data.projectName
        bottomSheetDialog.findViewById<TextView>(R.id.tv_people)?.text = data.totalContributor.toString()
        bottomSheetDialog.findViewById<LinearProgressIndicator>(R.id.pg_current)?.progress = data.progress
        bottomSheetDialog.findViewById<TextView>(R.id.tv_progress)?.text = "현재 ${data.progress}% 진행됐습니다"
        bottomSheetDialog.findViewById<TextView>(R.id.tv_total_distance)?.text = data.totalDistance
        bottomSheetDialog.findViewById<TextView>(R.id.tv_remain_distance)?.text = data.remainDistance
        bottomSheetDialog.findViewById<TextView>(R.id.tv_start_date)?.text = data.createDate
        bottomSheetDialog.findViewById<TextView>(R.id.tv_end_date)?.text = data.endDate
        bottomSheetDialog.findViewById<TextView>(R.id.tv_memo)?.text = data.memo

        bottomSheetDialog.findViewById<MaterialCardView>(R.id.btn_join_relay)?.setOnClickListener {
            viewModel.joinRelay(data.projectId)
        }
    }

    private fun showCannotCreateDistanceProjectDialog(){
        MaterialAlertDialogBuilder(mainActivity)
            .setTitle("잠시만요!")
            .setMessage("반경 50m 이내에 이어할 수 있는 거리 기반 릴레이가 존재합니다. \n" +
                    "릴레이를 이어 받아 완성해주세요!")
            .setPositiveButton("확인") { _, _ -> }
            .show()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateButtonClick(name: String, distance: Int, endDate: String) {
        val cur = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
        viewModel.createDistanceRelay(
            prefManager.getUserId(),
            name,
            DateFormatter.curMsToShorFormat(),
            DateFormatter.koreanToShortFormat(endDate),
            distance,
            Point(cur.latitude, cur.longitude)
        )
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
                    showSnackBar("path!")
                }
                R.id.fab_create_distance -> {
                    val current = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)!!
                    viewModel.isExistDistanceRelay(current.latitude, current.longitude)
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