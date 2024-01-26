package com.gdd.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.PathInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.gdd.presentation.LoginActivity
import com.gdd.presentation.MainActivity
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentLoginBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFade
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind, R.layout.fragment_login
) {
    private lateinit var loginActivity: LoginActivity
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginActivity = _activity as LoginActivity

        // 뒤로 가기 버튼 제어
        loginActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.layoutLoginInputs.visibility == View.VISIBLE) {
                        setDefaultUi()
                    } else {
                        requireActivity().finish()
                    }
                }
            })

        // DataBinding
        binding.loginViewModel = loginViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        registerListener()
        registerObserve()
    }

    private fun registerListener(){
        binding.btnSignin.setOnClickListener {
            if (binding.layoutLoginInputs.visibility == View.GONE){
                setLoginUi()
            } else {
                loginViewModel.login()
            }
        }

        binding.btnSignup.setOnClickListener {
            startActivity(Intent(loginActivity,SignupActivity::class.java))
        }
    }

    private fun registerObserve(){
        loginViewModel.loginResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                showSnackBar("로그인에 성공했습니다.")
                startActivity(Intent(_activity,MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                })
            } else {
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast("네트워크 오류")
                    }
                }
                startActivity(Intent(_activity,MainActivity::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }

        loginViewModel.inputErrorString.observe(viewLifecycleOwner){ event ->
            event.getContentIfNotHandled()?.let {
                if (!it) showSnackBar(_activity.getString(R.string.signin_empty_input))
            }
        }
    }

    private fun setLoginUi() {
        binding.btnSignup.visibility = View.GONE

        val materialFade = MaterialFade().apply {
            duration = 700L
            interpolator = PathInterpolator(0f, 0f, 0f, 1f)
        }
        TransitionManager.beginDelayedTransition(binding.layoutRoot, materialFade)
        binding.layoutLoginInputs.visibility = View.VISIBLE
    }

    private fun setDefaultUi() {
        binding.btnSignup.visibility = View.VISIBLE
        val materialFade = MaterialFade().apply {
            duration = 300L
            interpolator = PathInterpolator(0f, 0f, 0f, 1f)
        }
        TransitionManager.beginDelayedTransition(binding.layoutRoot, materialFade)
        binding.layoutLoginInputs.visibility = View.GONE
    }
}