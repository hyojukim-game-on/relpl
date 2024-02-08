package com.gdd.presentation.relay.relaying

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainActivity
import com.gdd.presentation.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentRelayStopInfoBinding
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

@AndroidEntryPoint
class RelayStopInfoFragment : BaseFragment<FragmentRelayStopInfoBinding>(
    FragmentRelayStopInfoBinding::bind, R.layout.fragment_relay_stop_info
) {
    @Inject
    lateinit var prefManager: PrefManager

    private lateinit var mainActivity: MainActivity
    private val relayStopInfoViewModel: RelayStopInfoViewModel by activityViewModels()

    private lateinit var naverMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnNext.isEnabled = false

        mainActivity = _activity as MainActivity
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

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
        relayStopInfoViewModel.callCount.observe(viewLifecycleOwner){count ->
            if (count >=3){
                binding.btnNext.isEnabled = true
                when(prefManager.getRelayingMode()){
                    PrefManager.RELAYING_MODE.DISTANCE->{
                        setUiDistance()
                    }
                    PrefManager.RELAYING_MODE.PATH->{
                        setUiPath()
                    }
                    PrefManager.RELAYING_MODE.NONE->{

                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUiCommon(){
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
        pathOverlay.coords = relayStopInfoViewModel.locationTrackingPointList.value!!.map { it.latLng }
        pathOverlay.map = naverMap
        binding.tvDistance.text =  relayStopInfoViewModel.locationTrackingPointList.value!!.zipWithNext().sumOf {
            it.first.latLng.distanceTo(it.second.latLng)
        }.toInt().toString() + "m"
        naverMap.moveCamera(CameraUpdate.fitBounds(
            LatLngBounds.from(pathOverlay.coords)
        ).animate(CameraAnimation.Easing))
    }

    private fun setUiPath(){
        setUiCommon()
    }


    private val mapCallBack = OnMapReadyCallback{map ->
        naverMap = map
        registerObserve()
        registerListener()
    }
}