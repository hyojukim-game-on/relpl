package com.gdd.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.gdd.domain.model.user.User
import com.gdd.domain.usecase.fcm.RegistFcmUseCase
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.base.intentSerializable
import com.gdd.presentation.databinding.ActivityMainBinding
import com.gdd.presentation.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "MainActivity_Genseong"

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(
    ActivityMainBinding::inflate
) {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.user = intent.intentSerializable("user", User::class.java)!!

        supportFragmentManager.beginTransaction().replace(R.id.layout_main_fragment, HomeFragment()).commit()

        Log.d(TAG, "onCreate: ${prefManager.getFcmToken()}")
        prefManager.getFcmToken()?.let {
            viewModel.registFcmToken(prefManager.getUserId(), it)
        }

        registerObserver()
    }

    private fun registerObserver(){
        viewModel.fcmResult.observe(this){
            if (it.isSuccess){
                Log.d(TAG, "registerObserver: @")
            }else{
                Log.d(TAG, "registerObserver: !")
            }
        }
    }

}