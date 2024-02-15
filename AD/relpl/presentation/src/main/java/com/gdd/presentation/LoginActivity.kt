package com.gdd.presentation

import android.os.Build
import android.os.Bundle
import android.view.View
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.databinding.ActivityLoginBinding
import com.gdd.presentation.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(
    ActivityLoginBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        } else {
            window.setDecorFitsSystemWindows(false)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_login_fragment,LoginFragment())
            .commit()
    }
}