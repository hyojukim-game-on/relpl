package com.gdd.presentation

import android.os.Bundle
import androidx.activity.viewModels
import com.gdd.domain.model.user.User
import com.gdd.domain.usecase.fcm.RegistFcmUseCase
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.base.intentSerializable
import com.gdd.presentation.databinding.ActivityMainBinding
import com.gdd.presentation.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate
) {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.user = intent.intentSerializable("user", User::class.java)!!

        supportFragmentManager.beginTransaction().replace(R.id.layout_main_fragment, HomeFragment()).commit()
    }

}