package com.gdd.presentation.signup

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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

        initView()
        registerListener()
        registerObserver()


    }

    private fun initView(){
        //처음에 버튼 클릭 막기
        binding.btnSend.isEnabled = false
    }

    private fun registerListener(){
        binding.btnSend.setOnClickListener {

        }

        binding.etPhone.editText!!.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.etPhone.editText!!.text.toString().trim().isNotEmpty())
                    activityViewModel.isValidPhone(binding.etPhone.editText!!.text.toString().trim())
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun registerObserver(){
        activityViewModel.phoneValidateResult.observe(viewLifecycleOwner){
            //유효한 형식의 핸드폰 번호일 때
            if (it){
                binding.btnSend.isEnabled = true
                binding.etPhone.isErrorEnabled = false
                binding.etPhone.helperText = resources.getString(R.string.signup_phonenumber_et_helper_valid)
            }
            //잘못된 형식의 핸드폰 번호일 때
            else{
                binding.btnSend.isEnabled = false
                binding.etPhone.helperText = ""
                binding.etPhone.isErrorEnabled = true
                binding.etPhone.error = resources.getString(R.string.signup_all_et_err)
            }
        }
    }
}