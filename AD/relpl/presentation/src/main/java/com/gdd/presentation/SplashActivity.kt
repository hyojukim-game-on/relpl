package com.gdd.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.databinding.ActivitySplashBinding
import javax.inject.Inject

class SplashActivity : BaseActivity<ActivitySplashBinding>(
    ActivitySplashBinding::inflate
) {
    @Inject
    lateinit var prefManager: PrefManager

    private val viewModel: SplashViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        prefManager.getAutoLoginState().let {
            if (it){
                val userId = prefManager.getUserId()
                viewModel.autoLogin(userId)
            }
            else{
                Handler().postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 2500)
            }
        }

        viewModel.autoLogin.observe(this){
            if (it.isSuccess)
        }

    }
}