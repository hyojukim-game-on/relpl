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
import com.gdd.presentation.databinding.FragmentSignupNicknameBinding
import com.gdd.retrofit_adapter.RelplException
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SignupNicknameFragment_Genseong"
@AndroidEntryPoint
class SignupNicknameFragment : BaseFragment<FragmentSignupNicknameBinding>(
    FragmentSignupNicknameBinding::bind, R.layout.fragment_signup_nickname
) {
    private lateinit var signupActivity: SignupActivity
    private val activityViewModel: SignupViewModel by activityViewModels()

    private var valid = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        signupActivity = _activity as SignupActivity

        initView()
        registerListener()
        registerObserver()
    }

    private fun initView(){}

    private fun registerListener(){
        binding.btnSignup.setOnClickListener {
            if (valid){
                //회원가입 요청 날리기
                activityViewModel.isDuplicatedNickname(binding.etNickname.editText!!.text.toString().trim())
                binding.etNickname.isEnabled = false
            }else{
                showToast(resources.getString(R.string.all_invalid_input))
            }
        }

        binding.etNickname.editText!!.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isNotEmpty()){
                    activityViewModel.isValidNickname(p0.toString().trim())
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun registerObserver(){
        activityViewModel.nicknameValidateResult.observe(viewLifecycleOwner){
            valid = it
            if (it){
                binding.etNickname.helperText = resources.getString(R.string.signup_nickname_et_valid)
            }else{
                binding.etNickname.error = resources.getString(R.string.signup_all_et_err)
            }
        }

        activityViewModel.nicknameDupResult.observe(viewLifecycleOwner){result ->
            if (result.isSuccess){
                if (!result.getOrNull()!!){
                    activityViewModel.nickname = binding.etNickname.editText!!.text.toString().trim()
                    showToast("사용 가능한 닉네임입니다")
                    activityViewModel.signUp()
                }else{
                    showToast("중복된 닉네임입니다")
                    binding.etNickname.isEnabled = true
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }

        activityViewModel.signUpResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                result.getOrNull()?.let {
                    activityViewModel.userId = it.id
                    activityViewModel.userNickname = it.nickname
                    signupActivity.moveToNextPage()
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }
    }
}