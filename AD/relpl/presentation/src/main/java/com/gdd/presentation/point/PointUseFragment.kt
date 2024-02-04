package com.gdd.presentation.point

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentPointUseBinding
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
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
}