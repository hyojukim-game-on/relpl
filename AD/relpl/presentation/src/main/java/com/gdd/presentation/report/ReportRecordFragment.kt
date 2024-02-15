package com.gdd.presentation.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentReportRecordBinding
import com.gdd.presentation.model.ReportRecordPoint
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportRecordFragment : BaseFragment<FragmentReportRecordBinding>(
    FragmentReportRecordBinding::bind, R.layout.fragment_report_record
) {
    private lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private val reportRecordViewModel: ReportRecordViewModel by viewModels()

    private lateinit var naverMap: NaverMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.layout_map, it).commit()
            }
        mapFragment.getMapAsync(mapReadyCallback)

        try {
            reportRecordViewModel.nickname.value = mainViewModel.user.nickname
            registerObserve()
        } catch (t: Throwable) {
            showToast("유저 정보 호출에 실패했습니다.")
        }
    }

    private val mapReadyCallback = OnMapReadyCallback { map ->
        naverMap = map
        val paddingSize = dpToPx(40f)
        naverMap.setContentPadding(paddingSize,paddingSize,paddingSize,paddingSize)
    }

    private fun registerObserve() {
        reportRecordViewModel.nickname.observe(viewLifecycleOwner) {
            binding.tvRecordNickname.text =
                mainActivity.getString(R.string.report_record_nickname, it)
        }

        reportRecordViewModel.reportRecordResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                result.getOrNull()?.let { list ->
                    if (list.isNotEmpty()){
                        binding.tvReportRecordCount.text =
                            mainActivity.getString(R.string.report_record_count, list.size.toString())
                        list.map {
                            Marker(
                                it.latLng,
                                OverlayImage.fromResource(R.drawable.ic_marker)
                            ).apply {
                                map = naverMap
                                iconTintColor = mainActivity.getColor(R.color.sage_red)
                                captionText = it.date.replace("-", ".")
                            }
                        }
                        naverMap.moveCamera(
                            CameraUpdate.fitBounds(getCameraBounds(list)).animate(CameraAnimation.Fly)
                        )
                    } else {
                        binding.tvReportRecordCount.text = "제보 내역이 없습니다."
                    }
                }
            } else {
                showSnackBar(result.exceptionOrNull()?.message ?: "제보 기록 호출 실패")
            }
        }
    }

    private fun getCameraBounds(list: List<ReportRecordPoint>): LatLngBounds {
        return LatLngBounds.from(
            list.map { it.latLng }
        )
    }

    private fun dpToPx(dp: Float): Int {
        return (dp * _activity.resources.displayMetrics.density + 0.5f).toInt()
    }
}