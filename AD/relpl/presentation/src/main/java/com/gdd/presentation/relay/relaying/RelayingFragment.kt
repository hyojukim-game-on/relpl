package com.gdd.presentation.relay.relaying

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.databinding.FragmentRelayingBinding
import com.gdd.presentation.home.HomeFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "RelayingFragment_Genseong"
@SuppressLint("SetTextI18n")
@AndroidEntryPoint
abstract class RelayingFragment : BaseFragment<FragmentRelayingBinding>(
    FragmentRelayingBinding::bind, R.layout.fragment_relaying
) {
    private lateinit var mainActivity: MainActivity
    protected val mainViewModel: MainViewModel by activityViewModels()
    protected val relayingViewModel: RelayingViewModel by viewModels()


    protected abstract val mapReady: OnMapReadyCallback
    protected abstract var naverMap: NaverMap?
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    protected var progressIsNotZero: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        // 뒤로가기 버튼 제어
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showToast("릴레이 중단은 \"그만하기\" 버튼을 눌러주세요")
                    parentFragmentManager.popBackStack(HomeFragment.HOME_FRAGMENT_BACKSTACK_NAME,FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            })

        try {
            binding.tvProgressNickname.text = "${mainViewModel.user.nickname}님이 함께한 거리"
        } catch (t: Throwable){}

        checkPermissions()
    }

    protected abstract fun registerListener()

    protected abstract fun registerObserve()

    protected abstract fun startRelay()

    protected abstract fun stopRelay()

    protected fun checkPermissions() {
        if (PermissionHelper.checkPermissionList(
                _activity,
                requestPermissions
            ).isNotEmpty()
        ) {
            PermissionHelper.requestPermissionList_fragment(
                this,
                requestPermissions.toTypedArray(),
                permissionGrantedListener,
                permissionDeniedListener
            )
        } else {
            permissionGrantedListener()
        }
    }


    protected var permissionGrantedListener: () -> Unit = {
        val mapFragment = childFragmentManager.findFragmentById(R.id.layout_map) as MapFragment?
            ?: MapFragment.newInstance().also {
                childFragmentManager.beginTransaction().add(R.id.layout_map, it).commit()
            }
        mapFragment.getMapAsync(mapReady)
        startRelay()
        bottomSheetSetting()
    }

    protected var permissionDeniedListener: () -> Unit = {
        MaterialAlertDialogBuilder(_activity)
            .setTitle("다음의 권한 허용이 필요합니다")
            .setMessage("- 알람 권한\n- 정확한 위치 권한\n설정으로 이동하시겠습니까?")
            .setNegativeButton("취소") { _, _ ->
                parentFragmentManager.popBackStack()
            }
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

    private val requestPermissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            listOf(Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_FINE_LOCATION)
        else
            listOf(Manifest.permission.ACCESS_FINE_LOCATION)

    private fun bottomSheetSetting() {
        // 바텀시트 제어
        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        naverMap?.setContentPadding(0, 0, 0, 0)
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        naverMap?.setContentPadding(0, 0, 0, binding.layoutBottomSheet.height)
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

    }
}