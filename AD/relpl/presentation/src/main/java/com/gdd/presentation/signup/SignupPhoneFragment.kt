package com.gdd.presentation.signup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupPhoneBinding

class SignupPhoneFragment : BaseFragment<FragmentSignupPhoneBinding>(
    FragmentSignupPhoneBinding::bind, R.layout.fragment_signup_phone
) {
    private lateinit var signupActivity: SignupActivity
    private val activityViewModel: SignupViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupActivity = _activity as SignupActivity

        binding.btnSend.setOnClickListener {
            activityViewModel.phoneNumber = binding.etPhone.editText!!.text.toString()
            signupActivity.moveToNextPage()
        }
    }
}