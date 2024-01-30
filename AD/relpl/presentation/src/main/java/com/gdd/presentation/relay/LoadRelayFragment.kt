package com.gdd.presentation.relay

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentLoadRelayBinding
import com.leinardi.android.speeddial.SpeedDialActionItem
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class LoadRelayFragment : BaseFragment<FragmentLoadRelayBinding>(
    FragmentLoadRelayBinding::bind, R.layout.fragment_load_relay
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: LoadRelayViewModel by viewModels()
    private lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        setFabSpeedDialUi()
    }

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
//                    showJoinGroupDialog()
                    showSnackBar("path!!")
                }
                R.id.fab_create_distance -> {
                    showSnackBar("distance!!")
                }
            }
            binding.fabCreateRelay.close()
            return@setOnActionSelectedListener true
        }
    }
}