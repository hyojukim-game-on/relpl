package com.gdd.presentation

import android.os.Bundle
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.databinding.ActivityLoginBinding
import com.gdd.presentation.login.LoginFragment

class LoginActivity : BaseActivity<ActivityLoginBinding>(
    ActivityLoginBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_login_fragment,LoginFragment())
            .commit()
    }
}