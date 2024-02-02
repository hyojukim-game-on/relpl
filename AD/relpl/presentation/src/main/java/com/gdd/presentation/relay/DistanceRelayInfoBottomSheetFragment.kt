package com.gdd.presentation.relay

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import co.kr.btmsheet.PersistBottomSheetFragment
import com.gdd.domain.model.relay.DistanceRelayInfo
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.databinding.BtmSheetDistanceRelayExpandBinding
import com.gdd.presentation.databinding.BtmSheetRelayCollapseBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.snackbar.Snackbar
import kotlin.math.exp

class DistanceRelayInfoBottomSheetFragment:
    PersistBottomSheetFragment<BtmSheetRelayCollapseBinding, BtmSheetDistanceRelayExpandBinding>(
        R.layout.btm_sheet_relay_collapse,
        R.layout.btm_sheet_distance_relay_expand
    ) {
    private lateinit var distanceRelayInfo: DistanceRelayInfo
    private val viewModel: LoadRelayViewModel by viewModels({requireParentFragment()})

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerObserver()
    }

    private fun registerObserver(){
        viewModel.distanceRelayInfoResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                result.getOrNull()?.let {
                    initCollapseView(it.projectName)
                    initExpandView(it)
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        Snackbar.make(binding.root, it.message, Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(binding.root, resources.getString(R.string.all_net_err), Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun initExpandView(distanceRelayInfo: DistanceRelayInfo){
        expandBinding.tvRelayName.text = distanceRelayInfo.projectName
        expandBinding.tvPeople.text = distanceRelayInfo.totalContributor.toString()
        expandBinding.pgCurrent.progress = distanceRelayInfo.progress
        expandBinding.tvProgress.text = " 현재 ${distanceRelayInfo.progress}% 진행됐습니다}"
        expandBinding.tvTotalDistance.text = distanceRelayInfo.totalDistance
        expandBinding.tvRemainDistance.text = distanceRelayInfo.remainDistance
        expandBinding.tvStartDate.text = distanceRelayInfo.createDate
        expandBinding.tvEndDate.text = distanceRelayInfo.endDate
        expandBinding.tvMemo.text = distanceRelayInfo.memo
    }

    private fun initCollapseView(projectName: String){
        collapseBinding.tvRelayName.text = distanceRelayInfo.projectName
    }

    companion object {

        private val TAG = DistanceRelayInfoBottomSheetFragment::class.simpleName

        fun show(
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
            data: DistanceRelayInfo
        ): DistanceRelayInfoBottomSheetFragment =
            fragmentManager.findFragmentByTag(TAG) as? DistanceRelayInfoBottomSheetFragment
                ?: DistanceRelayInfoBottomSheetFragment().apply {
                    distanceRelayInfo = data
                    fragmentManager.beginTransaction()
                        .replace(containerViewId, this, TAG)
                        .commitAllowingStateLoss()
                }

        fun dismiss(
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
        ): DistanceRelayInfoBottomSheetFragment {
            val f = fragmentManager.findFragmentByTag(TAG) as? DistanceRelayInfoBottomSheetFragment
                ?: DistanceRelayInfoBottomSheetFragment()
            fragmentManager.beginTransaction()
                .remove(f)
                .commit()
            return f
        }
    }

}
