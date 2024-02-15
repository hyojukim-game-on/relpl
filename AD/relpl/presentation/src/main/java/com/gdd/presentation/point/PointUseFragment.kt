package com.gdd.presentation.point

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.DEFAULT_ARGS_KEY
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentPointUseBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PointUseFragment : BaseFragment<FragmentPointUseBinding>(
    FragmentPointUseBinding::bind, R.layout.fragment_point_use
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val pointUseViewModel: PointUseViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // DataBinding
        binding.lifecycleOwner = viewLifecycleOwner
        binding.pointUseViewModel = pointUseViewModel

        try {
            pointUseViewModel.setNickname(mainViewModel.user.nickname)
        } catch (t: Throwable){
            showToast("회원 정보 호출에 실패했습니다.")
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.center_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.center_map, it).commit()
            }
        mapFragment.getMapAsync(mapReadyCallback)

        registerObserve()

        pointUseViewModel.getPointInfo()
    }

    private fun registerObserve(){
        pointUseViewModel.userId.observe(viewLifecycleOwner){
            if (it != -1L){
                createBarcode(it.toString())
            } else {
                showToast("회원 정보 호출에 실패했습니다.")
            }
        }

        pointUseViewModel.point.observe(viewLifecycleOwner){
            if (it == -1){
                showToast("포인트 정보 호출에 실패했습니다.")
            }
        }
    }

    private fun createBarcode(content: String) {
        try {
            val widthPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 300f,
                resources.displayMetrics
            ).toInt()
            val heightPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 80f,
                resources.displayMetrics
            ).toInt()
            val barcodeEncoder = BarcodeEncoder()
            barcodeEncoder.encodeBitmap(
                content,
                BarcodeFormat.CODE_128,
                widthPx,
                heightPx
            ).let { bitmap ->
                binding.ivPointBarcode.setImageBitmap(bitmap)
            }
        } catch (t: Throwable) {
            showToast("바코드 생성에 실패했습니다.")
        }
    }

    private val mapReadyCallback = OnMapReadyCallback { map ->
        map.uiSettings.apply {
            isTiltGesturesEnabled = false
            isRotateGesturesEnabled = false
            isZoomControlEnabled = false
        }
        Marker(LatLng(36.106010, 128.418697)).apply {
            icon = OverlayImage.fromResource(R.drawable.ic_marker)
            iconTintColor = _activity.getColor(R.color.sage_green_dark)
            this.map = map
        }
        val dongCenterCameraUpdate =
            CameraUpdate.scrollTo(LatLng(36.106010, 128.418697)).animate(CameraAnimation.Linear)
                .finishCallback {
                    map.moveCamera(
                        CameraUpdate.zoomTo(16.0).animate(CameraAnimation.Linear)
                    )
                }
        map.moveCamera(dongCenterCameraUpdate)
        map.addOnCameraIdleListener {
            map.moveCamera(dongCenterCameraUpdate)
        }
    }
}