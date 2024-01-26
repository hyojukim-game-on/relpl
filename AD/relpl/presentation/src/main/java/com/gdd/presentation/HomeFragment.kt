package com.gdd.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentHomeBinding
import com.gdd.presentation.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind, R.layout.fragment_home
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profile.setOnClickListener {
            binding.point.setTextColor(_activity.getColor(R.color.black))
            parentFragmentManager.beginTransaction()
                .addSharedElement(binding.ivProfile,"profile_image")
                .replace(R.id.layout_main_fragment,ProfileFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}