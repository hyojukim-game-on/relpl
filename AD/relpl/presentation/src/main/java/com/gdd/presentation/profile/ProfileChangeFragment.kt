package com.gdd.presentation.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentProfileBinding
import com.gdd.presentation.databinding.FragmentProfileChangeBinding


class ProfileChangeFragment : BaseFragment<FragmentProfileChangeBinding>(
    FragmentProfileChangeBinding::bind, R.layout.fragment_profile_change
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}