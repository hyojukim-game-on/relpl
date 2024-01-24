package com.gdd.presentation.login

import android.os.Bundle
import android.view.View
import android.view.animation.PathInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.transition.TransitionManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentLoginBinding
import com.google.android.material.transition.MaterialFade


class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind, R.layout.fragment_login
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.layoutLoginInputs.visibility == View.VISIBLE) {
                        setDefaultUi()
                    } else {
                        requireActivity().finish()
                    }
                }
            })

        binding.btnSignin.setOnClickListener {
            setLoginUi()
        }

    }

    private fun setLoginUi() {
        binding.btnSignup.visibility = View.GONE

        val materialFade = MaterialFade().apply {
            duration = 700L
            interpolator = PathInterpolator(0f, 0f, 0f, 1f)
        }
        TransitionManager.beginDelayedTransition(binding.root, materialFade)
        binding.layoutLoginInputs.visibility = View.VISIBLE
    }

    private fun setDefaultUi() {
        binding.btnSignup.visibility = View.VISIBLE
        binding.layoutLoginInputs.visibility = View.GONE
    }
}