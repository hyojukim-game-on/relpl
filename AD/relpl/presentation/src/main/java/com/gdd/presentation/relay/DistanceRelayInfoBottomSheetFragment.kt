package com.gdd.presentation.relay

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import co.kr.btmsheet.PersistBottomSheetFragment
import com.gdd.presentation.R
import com.gdd.presentation.databinding.BtmSheetDistanceRelayExpandBinding
import com.gdd.presentation.databinding.BtmSheetRelayCollapseBinding

class DistanceRelayInfoBottomSheetFragment:
    PersistBottomSheetFragment<BtmSheetRelayCollapseBinding, BtmSheetDistanceRelayExpandBinding>(
        R.layout.btm_sheet_relay_collapse,
        R.layout.btm_sheet_distance_relay_expand
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collapseBinding.viewSelect.setOnClickListener {
            expand()
        }
    }

    companion object {

        private val TAG = DistanceRelayInfoBottomSheetFragment::class.simpleName

        fun show(
            fragmentManager: FragmentManager,
            @IdRes containerViewId: Int,
        ): DistanceRelayInfoBottomSheetFragment =
            fragmentManager.findFragmentByTag(TAG) as? DistanceRelayInfoBottomSheetFragment
                ?: DistanceRelayInfoBottomSheetFragment().apply {
                    fragmentManager.beginTransaction()
                        .replace(containerViewId, this, TAG)
                        .commitAllowingStateLoss()
                }
    }

}
