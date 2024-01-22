package com.gdd.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.databinding.ActivityMainBinding
import com.gdd.presentation.databinding.ActivitySignupBinding
import com.gdd.presentation.signup.DepthPageTransformer
import com.gdd.presentation.signup.SignupViewModel
import com.gdd.presentation.signup.SignupViewPagerAdapter

class SignupActivity : BaseActivity<ActivitySignupBinding>(
    ActivitySignupBinding::inflate
) {
    private val viewModel: SignupViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewPager()
    }

    private fun initViewPager(){
        val viewPagerAdapter = SignupViewPagerAdapter(this)
        binding.vpInput.apply {
            adapter = viewPagerAdapter
            setPageTransformer(DepthPageTransformer())
        }
    }
}