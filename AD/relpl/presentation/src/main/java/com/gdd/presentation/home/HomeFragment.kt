package com.gdd.presentation.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdd.presentation.LoginActivity
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.base.distanceFormat
import com.gdd.presentation.base.pointFormat
import com.gdd.presentation.databinding.FragmentHomeBinding
import com.gdd.presentation.history.HistoryFragment
import com.gdd.presentation.profile.ProfileFragment
import com.gdd.presentation.rank.RankFragment
import com.gdd.presentation.relay.LoadRelayFragment
import com.gdd.presentation.relay.relaying.DistanceRelayingFragment
import com.gdd.presentation.relay.relaying.PathRelayingFragment
import com.gdd.presentation.report.ReportFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val TAG = "HomeFragment_Genseong"
@AndroidEntryPoint
class HomeFragment: BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind, R.layout.fragment_home
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var mainActivity: MainActivity

    private var backPressedTime = 0L

    @Inject
    lateinit var prefManager: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                   if (System.currentTimeMillis() - backPressedTime < 2000){
                       mainActivity.finish()
                   }else{
                       backPressedTime = System.currentTimeMillis()
                       showToast(resources.getString(R.string.all_finish_app))
                   }
                }
            })

        if (prefManager.getRelayingMode() != PrefManager.RELAYING_MODE.NONE){
            MaterialAlertDialogBuilder(_activity)
                .setMessage("릴레이가 진행중입니다.\n플로깅 시작 버튼을 누르면\n진행중인 릴레이 정보를 확인 할 수 있습니다.")
                .setPositiveButton("확인"){_,_->}
                .show()
        }

        checkPermission()
        initView()
        registerObserver()
        registerListener()

        for (i in 0 until parentFragmentManager.backStackEntryCount){
            Log.d(TAG, "popstack: ${parentFragmentManager.getBackStackEntryAt(i)}")
        }
    }

    private fun registerObserver(){
        mainViewModel.userInfoResult.observe(viewLifecycleOwner){

        }
    }

    private fun registerListener(){
        binding.profile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .addSharedElement(binding.ivProfile,"profile_image")
                .replace(R.id.layout_main_fragment,ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.ivLogout.setOnClickListener {
            MaterialAlertDialogBuilder(_activity)
                .setMessage("로그아웃 하시겠습니까?")
                .setNegativeButton("취소") { dialog, which ->

                }
                .setPositiveButton("확인") { dialog, which ->
                    homeViewModel.logout()
                    startActivity(Intent(_activity,LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
                .show()
        }

        binding.reportCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.layout_main_fragment,ReportFragment())
                .addToBackStack(HOME_FRAGMENT_BACKSTACK_NAME)
                .commit()
        }

        binding.cvStartRelay.setOnClickListener {
            when(prefManager.getRelayingMode()){
                PrefManager.RELAYING_MODE.DISTANCE->{
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.layout_main_fragment, DistanceRelayingFragment())
                        .addToBackStack(HOME_FRAGMENT_BACKSTACK_NAME)
                        .commit()
                }
                PrefManager.RELAYING_MODE.PATH->{
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.layout_main_fragment, PathRelayingFragment())
                        .addToBackStack(HOME_FRAGMENT_BACKSTACK_NAME)
                        .commit()
                }
                PrefManager.RELAYING_MODE.NONE->{
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.layout_main_fragment, LoadRelayFragment())
                        .addToBackStack(HOME_FRAGMENT_BACKSTACK_NAME)
                        .commit()
                }
            }

        }

        binding.rankCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.layout_main_fragment, RankFragment())
                .addToBackStack(HOME_FRAGMENT_BACKSTACK_NAME)
                .commit()
        }

        binding.recordCard.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.layout_main_fragment, HistoryFragment())
                .addToBackStack(HOME_FRAGMENT_BACKSTACK_NAME)
                .commit()
        }
    }

    private fun initView(){
        binding.tvNickname.text = resources.getString(R.string.home_welcome, mainViewModel.user.nickname)
        binding.tvPoint.text = mainViewModel.user.totalCoin.pointFormat()
        binding.tvDistance.text = resources.getString(R.string.home_total_distance, mainViewModel.user.totalDistance.distanceFormat())

        if (mainViewModel.user.imageUri != null){
            Glide.with(this)
                .load(mainViewModel.user.imageUri)
                .fitCenter()
                .apply(RequestOptions().circleCrop())
                .into(binding.ivProfile)
        }
    }

    private fun checkPermission() {
        val permissionList =
            mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS)
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }

        if (PermissionHelper.checkPermissionList(_activity,permissionList).isNotEmpty()) {
            PermissionHelper.requestPermissionList_fragment(this, permissionList.toTypedArray(),
                deniedListener = {
                    showPermissionDialog()
                })
        }
    }

    private fun showPermissionDialog(){
        MaterialAlertDialogBuilder(_activity)
            .setTitle("릴플과 함께하려면 다음의 권한이 필요해요")
            .setMessage("- 알람\n- 정확한 위치\n- 갤러리 접근\n- 카메라\n\n위의 권한이 없으면 많은 기능들을 이용하지 못합니다. 설정으로 이동할까요?")
            .setNegativeButton("취소") { _, _ -> }
            .setPositiveButton("확인") { _, _ ->
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${_activity.packageName}")
                    ).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            .show()
    }

    companion object{
        const val HOME_FRAGMENT_BACKSTACK_NAME = "home_fragment_stack"
    }
}