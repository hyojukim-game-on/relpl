package com.gdd.presentation.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.LoginActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.distanceFormat
import com.gdd.presentation.base.pointFormat
import com.gdd.presentation.databinding.FragmentHomeBinding
import com.gdd.presentation.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind, R.layout.fragment_home
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        registerListener()
    }

    private fun registerListener(){
        binding.profile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .addSharedElement(binding.ivProfile,"profile_image")
                .replace(R.id.layout_main_fragment,ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.ivLogout.setOnClickListener {
            homeViewModel.logout()
            startActivity(Intent(_activity,LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    private fun initView(){
        binding.tvNickname.text = resources.getString(R.string.home_welcome, mainViewModel.user.nickname)
        binding.tvPoint.text = mainViewModel.user.totalCoin.pointFormat()
        binding.tvDistance.text = resources.getString(R.string.home_total_distance, mainViewModel.user.totalDistance.distanceFormat())
    }
}