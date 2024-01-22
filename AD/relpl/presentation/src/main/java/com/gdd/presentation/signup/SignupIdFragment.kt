package com.gdd.presentation.signup

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupIdBinding


class SignupIdFragment : BaseFragment<FragmentSignupIdBinding>(
    FragmentSignupIdBinding::bind, R.layout.fragment_signup_id
) {
    private lateinit var signupActivity: SignupActivity
    private val activityViewModel: SignupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupActivity = _activity as SignupActivity

        registerListener()
    }

    private fun registerListener(){
        binding.btnNext.setOnClickListener {
            signupActivity.moveToNextPage()
        }
    }

}