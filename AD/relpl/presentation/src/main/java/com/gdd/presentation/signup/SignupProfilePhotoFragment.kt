package com.gdd.presentation.signup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupProfilePhotoBinding
import kotlin.math.log

private const val TAG = "SignupProfilePhotoFragm_Genseong"
class SignupProfilePhotoFragment : BaseFragment<FragmentSignupProfilePhotoBinding>(
    FragmentSignupProfilePhotoBinding::bind, R.layout.fragment_signup_profile_photo
) {
    private lateinit var signUpActivity: SignupActivity
    private val activityViewModel: SignupViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpActivity = _activity as SignupActivity

        initView()
        registerListener()
        registerObserver()
    }

    private fun initView(){
        Log.d(TAG, "initView: ${signUpActivity.nickname}")
        binding.tvWelcome.text = resources.getString(R.string.signup_photo_welcome, signUpActivity.nickname)
    }

    private fun registerListener(){
        binding.btnAddPhoto.setOnClickListener {
            Log.d(TAG, "registerListener: ${signUpActivity.nickname}")
        }
    }

    private fun registerObserver(){

    }
}