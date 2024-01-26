package com.gdd.presentation

import android.os.Bundle
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.databinding.ActivityMainBinding
import com.gdd.presentation.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(R.id.layout_main_fragment, HomeFragment()).commit()
    }
}