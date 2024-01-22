package com.gdd.presentation.signup

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupVerifyBinding

class SignupVerifyFragment : BaseFragment<FragmentSignupVerifyBinding>(
    FragmentSignupVerifyBinding::bind, R.layout.fragment_signup_verify
) {
    private val activityViewModel: SignupViewModel by activityViewModels()
    private var verificationCode = ""
    private lateinit var codeArr : Array<EditText>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvVerifyContent.text = "${activityViewModel.phoneNumber}\n ${resources.getString(R.string.signup_verify_content)}"

        codeArr = arrayOf(
            binding.etCode1,
            binding.etCode2,
            binding.etCode3,
            binding.etCode4,
            binding.etCode5,
            binding.etCode6
        )

        onDeleteListener()
        setCodeOnChangedListener()
    }
    private fun onDeleteListener(){
        for (idx in 1 .. 5) codeArr[idx].setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL){
                codeArr[idx - 1].requestFocus()
            }
            false
        }
    }

    private fun setCodeOnChangedListener(){
        for (idx in 0 until codeArr.size - 1) codeArr[idx].addTextChangedListener{
            if (codeArr[idx].length() == 1){
                codeArr[idx+1].requestFocus()
                codeArr[idx+1].text = null
            }
        }
    }

}