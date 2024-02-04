package com.gdd.presentation.signup

import android.content.Context
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
import com.gdd.presentation.databinding.FragmentSignupPhoneBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


private const val TAG = "SignupPhoneFragment_Genseong"
@AndroidEntryPoint
class SignupPhoneFragment : BaseFragment<FragmentSignupPhoneBinding>(
    FragmentSignupPhoneBinding::bind, R.layout.fragment_signup_phone
) {
    private lateinit var signupActivity: SignupActivity
    private val activityViewModel: SignupViewModel by activityViewModels()
    val auth = Firebase.auth

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
            activityViewModel.isDuplicatedPhone("010${binding.etPhone.editText!!.text.toString().trim()}")
            binding.etPhone.isEnabled = false
        }

        binding.etPhone.editText!!.addTextChangedListener (object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.toString().trim().isNotEmpty())
                    activityViewModel.isValidPhone(p0!!.toString().trim())
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

        activityViewModel.phoneDupResult.observe(viewLifecycleOwner){result ->
            if (result.isSuccess){
                if (!result.getOrNull()!!){
                    showToast(resources.getString(R.string.profile_change_usable_phone))
                    sendVerificationCode()
                }else{
                    showToast(resources.getString(R.string.profile_change_dup_phone))
                    binding.etPhone.isEnabled = true
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showToast(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }
    }

    private fun sendVerificationCode(){
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }
            override fun onVerificationFailed(e: FirebaseException) {
                showToast(resources.getString(R.string.all_verify_send_fail))
                binding.etPhone.editText?.isEnabled = true
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                activityViewModel.verificationId = verificationId
                activityViewModel.phoneNumber = "010"+binding.etPhone.editText!!.text.toString().trim()
                showToast(resources.getString(R.string.all_verify_send_success))
                signupActivity.moveToNextPage()
            }
        }
        val phoneNumber = "+82010${binding.etPhone.editText!!.text.trim()}"
        Log.d(TAG, "registerListener: $phoneNumber")

        val optionsCompat =  PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(90L, TimeUnit.SECONDS)
            .setActivity(signupActivity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(optionsCompat)
        auth.setLanguageCode("kr")
    }
}