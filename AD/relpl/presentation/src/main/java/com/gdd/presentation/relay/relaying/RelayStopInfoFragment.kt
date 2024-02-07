package com.gdd.presentation.relay.relaying

import android.os.Bundle
import android.view.View
import com.gdd.presentation.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentRelayStopInfoBinding
import com.naver.maps.map.MapFragment
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RelayStopInfoFragment : BaseFragment<FragmentRelayStopInfoBinding>(
    FragmentRelayStopInfoBinding::bind, R.layout.fragment_relay_stop_info
) {
    @Inject
    lateinit var prefManager: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.layout_map, it).commit()
            }
        mapFragment.getMapAsync(mapCallBack)
    }


    private val mapCallBack = OnMapReadyCallback{map ->
        when(prefManager.getRelayingMode()){
            PrefManager.RELAYING_MODE.DISTANCE->{

            }
            PrefManager.RELAYING_MODE.PATH->{

            }
            PrefManager.RELAYING_MODE.NONE->{

            }
        }
    }
}