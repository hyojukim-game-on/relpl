package com.gdd.presentation.signup

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupVerifyBinding
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SignupVerifyFragment_Genseong"
@AndroidEntryPoint
class SignupVerifyFragment : BaseFragment<FragmentSignupVerifyBinding>(
    FragmentSignupVerifyBinding::bind, R.layout.fragment_signup_verify
) {
    private val activityViewModel: SignupViewModel by activityViewModels()
    private lateinit var signupActivity: SignupActivity

    private var verificationCode = ""
    private lateinit var codeArr : Array<EditText>
    private val auth = Firebase.auth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupActivity = _activity as SignupActivity

        initView()
        onDeleteListener()
        setCodeOnChangedListener()
        registerListener()
    }

    private fun initView(){
        binding.tvVerifyContent.text = resources.getString(R.string.signup_verify_content, activityViewModel.phoneNumber)

        codeArr = arrayOf(
            binding.etCode1,
            binding.etCode2,
            binding.etCode3,
            binding.etCode4,
            binding.etCode5,
            binding.etCode6
        )
    }

    private fun registerListener(){
        binding.btnVerify.setOnClickListener {
            calcCode()
            Log.d(TAG, "registerListener: $verificationCode")
            if(verificationCode.length < 6)
                showToast("인증코드 형식이 유효하지 않습니다")
            else{
                val credential = PhoneAuthProvider.getCredential(activityViewModel.verificationId, verificationCode)
                signInWithPhoneAuthCredential(credential)
            }
        }
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

    private fun calcCode(){
        verificationCode = ""
        var code = codeArr.forEach {
            verificationCode += it.text
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(signupActivity) { task ->
                if (task.isSuccessful) {
                    showToast("인증에 성공했습니다.")
                    signupActivity.moveToNextPage()
                }
                else {
                    //인증실패
                    showToast("인증에 실패했습니다.")
                }
            }
    }

}