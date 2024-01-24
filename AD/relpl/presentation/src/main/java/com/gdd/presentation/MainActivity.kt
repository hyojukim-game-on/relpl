package com.gdd.presentation

import android.os.Bundle
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate
) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}