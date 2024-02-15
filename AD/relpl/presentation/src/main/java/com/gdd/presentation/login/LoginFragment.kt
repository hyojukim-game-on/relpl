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
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentLoginBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.transition.MaterialFade
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "LoginFragment_ksh"
@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind, R.layout.fragment_login
) {
    @Inject
    lateinit var prefManager: PrefManager

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

        binding.cbAutoLogin.setOnCheckedChangeListener { _, b ->
            prefManager.setAutoLoginState(b)
        }
    }

    private fun registerObserve(){
        loginViewModel.loginResult.observe(viewLifecycleOwner){ result ->
            result?.let {
//                initFirebase(it.getOrNull())
            }
            if (result.isSuccess){
                showSnackBar("로그인에 성공했습니다.")
                startActivity(Intent(_activity,MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("user", result.getOrNull())
                })
                loginActivity.finish()
            } else {
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
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

    // firebase push 관련
//    private fun initFirebase(user: User?) {
//
//        if (user == null) return
//
//        // FCM 토큰 수신
//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w(TAG, "FCM 토큰 얻기에 실패하였습니다.", task.exception)
//                return@OnCompleteListener
//            }
//            // token log 남기기
//            Log.d(TAG, "token: ${task.result?:"task.result is null"}")
//            task.result?.let {
//                loginViewModel.registFcmToken(it)
//            }
//        })
//    }

}