package com.gdd.presentation

import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.gdd.domain.model.user.User
import com.gdd.domain.usecase.fcm.RegistFcmUseCase
import com.gdd.presentation.base.BaseActivity
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.base.intentSerializable
import com.gdd.presentation.databinding.ActivityMainBinding
import com.gdd.presentation.home.HomeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_main_fragment, HomeFragment())
            .addToBackStack(null)
            .commit()

        Log.d(TAG, "onCreate: ${prefManager.getFcmToken()}")
        prefManager.getFcmToken()?.let {
            viewModel.registFcmToken(prefManager.getUserId(), it)
        }

        registerObserver()
    }

    override fun onStart() {
        super.onStart()
        if (!isOnLocationSetting()){
            MaterialAlertDialogBuilder(this)
                .setTitle("위치 설정을 켜주세요")
                .setMessage("위치 설정이 꺼져있으면 릴플을 이용 할 수 없습니다. 위치 설정으로 이동하시겠습니까?")
                .setNegativeButton("종료하기"){_,_ ->
                    finish()
                }
                .setPositiveButton("설정하기"){_,_ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .show()
        }
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

    fun showLoadingView(){
        binding.loadingView.visibility = View.VISIBLE
    }

    fun dismissLoadingView(){
        binding.loadingView.visibility = View.GONE
    }

    private fun isOnLocationSetting(): Boolean{
        val locationManager = baseContext.getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }
}