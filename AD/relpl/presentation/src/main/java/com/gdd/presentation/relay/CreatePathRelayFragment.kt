package com.gdd.presentation.relay

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gdd.domain.model.Point
import com.gdd.domain.model.relay.PathRelayInfo
import com.gdd.domain.model.tracking.RelayPathData
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.LoadingDialog
import com.gdd.presentation.base.location.LocationProviderController
import com.gdd.presentation.base.toLatLng
import com.gdd.presentation.databinding.FragmentCreatePathRelayBinding
import com.gdd.presentation.mapper.DateFormatter
import com.gdd.presentation.relay.relaying.PathRelayingFragment
import com.gdd.retrofit_adapter.RelplException
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
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CreatePathRelayFragment_Genseong"
@AndroidEntryPoint
class CreatePathRelayFragment : BaseFragment<FragmentCreatePathRelayBinding>(
    FragmentCreatePathRelayBinding::bind, R.layout.fragment_create_path_relay
), CreatePathDialogClickInterface {
    private val viewModel : CreatePathRelayViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    private lateinit var naverMap: NaverMap
    private lateinit var locationProviderController: LocationProviderController

    private var shortPathList = listOf<LatLng>()
    private var recommendPathList = listOf<LatLng>()
    private lateinit var shortPath : PathOverlay
    private lateinit var recommendPath: PathOverlay

    private var startPointMarker: Marker = Marker()
    private var endPointMarker: Marker = Marker()

    @Inject
    lateinit var prefManager: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        locationProviderController = LocationProviderController(mainActivity, viewLifecycleOwner)

        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.layoutBottomSelectPath.visibility == View.VISIBLE) {
                        setDefaultUi()
                    } else {
                        parentFragmentManager.popBackStack()
                    }
                }
            })
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.map_fragment, it).commit()
            }
        mapFragment.getMapAsync(mapReadyCallback)
        mainActivity.showLoadingView()

        registerListener()
        registerObserver()
    }

    private fun registerListener(){
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

        binding.btnSetDestination.setOnClickListener {
            mainActivity.showLoadingView()

            locationProviderController.getCurrnetLocation {task ->
                if (!task.isCanceled){
                    if (task.isSuccessful) {
                        task.result.also {
                            viewModel.recommendPath(
                                Point(it.longitude, it.latitude),
                                Point(naverMap.cameraPosition.target.longitude,
                                    naverMap.cameraPosition.target.latitude)
                            )
                        }
                    } else {
                        showSnackBar("위치정보 호출에 실패했습니다.")
                    }
                }
            }
        }

        binding.recommendPathOverlay.setOnClickListener {
            drawPathRecommendSelected()
        }

        binding.shortestPathOverlay.setOnClickListener {
            drawPathShortSelected()
        }

        binding.btnReSetDestination.setOnClickListener {
            setDefaultUi()
        }

        binding.btnSetPath.setOnClickListener {
            if (binding.recommendPathOverlay.visibility == View.GONE){
                viewModel.isRecommendedPathSelected = true
                viewModel.recommendedPathResult.value!!.getOrNull()?.let{
                    viewModel.selectedPathId = it.recommendId
                    viewModel.selectedPathDistance = it.recommendTotalDistance
                    viewModel.startCoordinate = it.recommendPath[0]
                    viewModel.endCoordinate = it.recommendPath.last()
                    viewModel.projectSelectedCoordinateTotalSize = recommendPathList.size
                }
                val dialog = CreatePathRelayDialog(this, viewModel.recommendedPathResult.value!!.getOrNull()!!.recommendTotalDistance)
                dialog.isCancelable = false
                dialog.show(this.childFragmentManager, "")
            }else{
                viewModel.isRecommendedPathSelected = false
                viewModel.recommendedPathResult.value!!.getOrNull()?.let {
                    viewModel.selectedPathId = it.shortestId
                    viewModel.selectedPathDistance = it.shortestTotalDistance
                    viewModel.startCoordinate = it.shortestPath[0]
                    viewModel.endCoordinate = it.shortestPath.last()
                    viewModel.projectSelectedCoordinateTotalSize = shortPathList.size
                }
                val dialog = CreatePathRelayDialog(this, viewModel.recommendedPathResult.value!!.getOrNull()!!.shortestTotalDistance)
                dialog.isCancelable = false
                dialog.show(this.childFragmentManager, "")
            }
        }
    }

    private fun drawPathRecommendSelected(){
        binding.recommendPathOverlay.visibility = View.GONE
        binding.shortestPathOverlay.visibility = View.VISIBLE

        shortPath.apply {
            map = null
            coords = shortPathList
            color = resources.getColor(R.color.text_gray)
            patternImage = OverlayImage.fromResource(R.drawable.ic_path_arrow)
            patternInterval = 60
            width = 25
            outlineWidth = 1
            map = naverMap
        }
        recommendPath.apply {
            map = null
            coords = recommendPathList
            color = resources.getColor(R.color.sage_green)
            patternImage = OverlayImage.fromResource(R.drawable.ic_path_arrow)
            patternInterval = 60
            width = 25
            outlineWidth = 1
            map = naverMap
        }

    }

    private fun drawPathShortSelected(){
        binding.recommendPathOverlay.visibility = View.VISIBLE
        binding.shortestPathOverlay.visibility = View.GONE

        recommendPath.apply {
            map = null
            coords = recommendPathList
            color = resources.getColor(R.color.text_gray)
            patternImage = OverlayImage.fromResource(R.drawable.ic_path_arrow)
            patternInterval = 60
            width = 25
            outlineWidth = 1
            map = naverMap
        }

        shortPath.apply {
            map = null
            coords = shortPathList
            color = resources.getColor(R.color.sage_green)
            patternImage = OverlayImage.fromResource(R.drawable.ic_path_arrow)
            patternInterval = 60
            width = 25
            outlineWidth = 1
            map = naverMap
        }
    }

    private fun registerObserver(){
        viewModel.recommendedPathResult.observe(viewLifecycleOwner){ result ->
            mainActivity.dismissLoadingView()
            if (result.isSuccess){
                result.getOrNull()?.let {
                    Log.d(TAG, "registerObserver: ${it.recommendPath.size}, ${it.shortestPath.size}")
                    binding.ivMarker.visibility = View.GONE
                    binding.layoutBottomSetDestination.visibility = View.GONE
                    binding.layoutBottomSelectPath.visibility = View.VISIBLE
                    binding.tvMoveScreen.visibility = View.GONE

                    binding.tvShorPathDistance.text = "총 ${it.shortestTotalDistance}m"
                    binding.tvRecommendPathDistance.text = "총 ${it.recommendTotalDistance}m"

                    recommendPathList = it.recommendPath.map {  p ->
                        p.toLatLng()
                    }

                    startPointMarker.apply {
                        map = null
                        position =  recommendPathList[0]
                        map = naverMap
                        icon = OverlayImage.fromResource(R.drawable.ic_marker)
                        iconTintColor =  resources.getColor(R.color.sage_blue)
                        captionText = "출발점"
                    }
                    endPointMarker.apply {
                        map = null
                        position =  recommendPathList.last()
                        map = naverMap
                        icon = OverlayImage.fromResource(R.drawable.ic_marker)
                        iconTintColor =  resources.getColor(R.color.sage_orange)
                        captionText = "도착점"
                    }

                    shortPathList = it.shortestPath.map { p ->
                        p.toLatLng()
                    }

                    recommendPath = PathOverlay()
                    shortPath = PathOverlay()

                    drawPathRecommendSelected()
                }
            }else {
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }

        viewModel.createPathRelayResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                result.getOrNull()?.let {
//                    showSnackBar(it.toString())
                  viewModel.joinRelay(it)
                }
            }else {
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }

        /**
         * 여기서 참여
         */
        viewModel.joinRelayResult.observe(viewLifecycleOwner){ result ->
            //참여 성공했으면 화면 전환
            if (result.isSuccess) {
                result.getOrNull()?.let {
                    //여기서 화면 전환
                    viewModel.getPathRelayInfo(it)
//                    showToast("$it 번 프로젝트 참여 성공")
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
            if (result.isSuccess) {
                result.getOrNull()?.let {
                        joinToPathRelay(it)
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

    private fun joinToPathRelay(relayInfo: PathRelayInfo){
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.saveRelayInfoToLocal(
                relayInfo.projectId,
                relayInfo.projectName,
                relayInfo.totalContributor,
                relayInfo.totalDistance,
                relayInfo.remainDistance,
                relayInfo.createDate,
                relayInfo.endDate,
                relayInfo.isPath,
                relayInfo.stopCoordinate.y,
                relayInfo.stopCoordinate.x
            )
            var flagIndex = relayInfo.route.indexOfFirst {
                it.x == relayInfo.stopCoordinate.x && it.y == relayInfo.stopCoordinate.y
            }
            viewModel.saveRelayPathData(
                relayInfo.route.mapIndexed{ index, it ->
                    RelayPathData(it.y, it.x, false, index <= flagIndex)
                }
            )
            prefManager.setRelayingMode(PrefManager.RELAYING_MODE.PATH)
            parentFragmentManager.beginTransaction()
                .replace(R.id.layout_main_fragment, PathRelayingFragment())
                .addToBackStack(null)
                .commit()
        }
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
    }

    private fun setDefaultUi(){
        startPointMarker.map = null
        endPointMarker.map = null
        binding.layoutBottomSelectPath.visibility = View.GONE
        binding.layoutBottomSetDestination.visibility = View.VISIBLE
        binding.ivMarker.visibility = View.VISIBLE
        binding.tvMoveScreen.visibility = View.VISIBLE
        shortPath.map = null
        recommendPath.map = null
    }

    override fun onCreateButtonClick(name: String, endDate: String) {

        viewModel.createPathRelay(
            prefManager.getUserId(),
            viewModel.selectedPathId,
            viewModel.selectedPathDistance,
            viewModel.projectSelectedCoordinateTotalSize,
            name,
            DateFormatter.curMsToShorFormat(),
            DateFormatter.koreanToShortFormat(endDate),
            viewModel.startCoordinate,
            viewModel.endCoordinate
        )

    }

    override fun onReSetButtonClick() {
        setDefaultUi()
    }

}