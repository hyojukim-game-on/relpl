package com.gdd.presentation.relay

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.domain.model.Point
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.location.LocationProviderController
import com.gdd.presentation.base.toLatLng
import com.gdd.presentation.databinding.FragmentCreatePathRelayBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.PathOverlay
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "CreatePathRelayFragment_Genseong"
@AndroidEntryPoint
class CreatePathRelayFragment : BaseFragment<FragmentCreatePathRelayBinding>(
    FragmentCreatePathRelayBinding::bind, R.layout.fragment_create_path_relay
) {
    private val viewModel : CreatePathRelayViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    private lateinit var naverMap: NaverMap
    private lateinit var locationProviderController: LocationProviderController

    private var shortPathList = listOf<LatLng>()
    private var recommendPathList = listOf<LatLng>()
    private lateinit var shortPath : PathOverlay
    private lateinit var recommendPath: PathOverlay

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        locationProviderController = LocationProviderController(mainActivity, viewLifecycleOwner)

        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.layoutBottomSelectPath.visibility == View.VISIBLE) {
                        binding.layoutBottomSelectPath.visibility = View.GONE
                        binding.layoutBottomSetDestination.visibility = View.VISIBLE
                        binding.ivMarker.visibility = View.VISIBLE
                        binding.tvMoveScreen.visibility = View.VISIBLE
                        shortPath.map = null
                        recommendPath.map = null
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
                }
            }
        }

        binding.btnSetDestination.setOnClickListener {
            locationProviderController.getCurrnetLocation {task ->
                if (!task.isCanceled){
                    if (task.isSuccessful) {
                        task.result.also {
                            viewModel.recommendPath(Point(it.longitude, it.latitude), Point(naverMap.cameraPosition.target.longitude, naverMap.cameraPosition.target.latitude))
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
    }

    private fun drawPathRecommendSelected(){
        binding.recommendPathOverlay.visibility = View.GONE
        binding.shortestPathOverlay.visibility = View.VISIBLE

        shortPath.apply {
            map = null
            coords = shortPathList
            color = resources.getColor(R.color.text_gray)
            map = naverMap
        }
        recommendPath.apply {
            map = null
            coords = recommendPathList
            color = resources.getColor(R.color.sage_green)
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
            map = naverMap
        }

        shortPath.apply {
            map = null
            coords = shortPathList
            color = resources.getColor(R.color.sage_green)
            map = naverMap
        }
    }

    private fun registerObserver(){
        viewModel.recommendedPathResult.observe(viewLifecycleOwner){ result ->
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
}