package com.gdd.presentation.signup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SignupPasswordFragment_Genseong"
@AndroidEntryPoint
class SignupPasswordFragment : BaseFragment<FragmentSignupPasswordBinding>(
    FragmentSignupPasswordBinding::bind, R.layout.fragment_signup_password
) {
    private lateinit var signupActivity: SignupActivity
    private val activityViewModel: SignupViewModel by activityViewModels()
    private var done = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupActivity = _activity as SignupActivity
        Log.d(TAG, "onViewCreated: ${activityViewModel.id}")
        
        initView()
        registerListener()
        registerObserver()
    }

    private fun initView(){
    }

    private fun registerListener(){
        binding.btnNext.setOnClickListener {
            if (done) {
                activityViewModel.pw = binding.etPw.editText!!.text.toString()
                signupActivity.moveToNextPage()
            }
            else
                showToast(resources.getString(R.string.all_invalid_input))
        }

        binding.etPw.editText?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().isNotEmpty()){
                    activityViewModel.isValidPw(p0!!.toString().trim())

                    //비밀번호 재확인란이 빈값이 아닐 경우 재확인과 일치 검사
                    if (binding.etPwRe.editText!!.text.toString().isNotEmpty()){
                        isSameWithPwRe(p0.toString())
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.etPwRe.editText?.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isSameWithPwRe()
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun registerObserver(){
        activityViewModel.pwValidateResult.observe(viewLifecycleOwner){
            //유효한 비밀번호
            if (it){
                binding.etPw.helperText = resources.getString(R.string.signup_pw_et_valid)
            }else{
                binding.etPw.error = resources.getString(R.string.signup_all_et_err)
                done = it
            }
        }
    }

    private fun isSameWithPwRe(pw: String = binding.etPw.editText!!.text.toString().trim()){
        //비밀번호가 유효하고
        if (activityViewModel.pwValidateResult.value == true){
            //비밀번호와 비밀번호 확인이 일치
            if (pw == binding.etPwRe.editText!!.text.toString().trim()) {
                binding.etPwRe.helperText = resources.getString(R.string.signup_repw_et_valid)
                done = true
            }
            else {
                binding.etPwRe.error = resources.getString(R.string.signup_repw_et_err)
                done = false
            }
        }else{
            binding.etPwRe.error = resources.getString(R.string.signup_repw_et_err)
            done = false
        }
    }

}