package com.gdd.presentation.report

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import com.gdd.presentation.MainActivity
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.databinding.FragmentReportBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "ReportFragment_Genseong"

class ReportFragment : BaseFragment<FragmentReportBinding>(
    FragmentReportBinding::bind, R.layout.fragment_report
) {
    private lateinit var mainActivity: MainActivity

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "registerListener: ${bottomSheetBehavior.state}")
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED){
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    } else {
                        parentFragmentManager.popBackStack()
                    }
                }
            })

        bottomSheetBehavior = BottomSheetBehavior.from(binding.layoutBottomSheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        checkLocationPermission()

        registerListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun registerListener() {
        binding.layoutBottomSheet.setOnTouchListener { _, _ ->
            true
        }

        binding.btnReport.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            Log.d(TAG, "registerListener: ${bottomSheetBehavior.state}")
        }
    }

    private fun checkLocationPermission() {
        if (!PermissionHelper.checkPermission(
                mainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            PermissionHelper.requestPermission_fragment(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION,
                deniedListener = locationPermissionDeniedListener
            )
        }
    }

    private val locationPermissionDeniedListener: () -> Unit = {
        MaterialAlertDialogBuilder(_activity)
            .setTitle("정확한 위치 권한 허용이 필요합니다")
            .setMessage("정확한 위치 권한을 허용하지 않을 경우 해당 기능을 이용할 수 없습니다. 설정으로 이동하시겠습니까?")
            .setNegativeButton("취소") { _, _ ->
                parentFragmentManager.popBackStack()
            }
            .setPositiveButton("확인") { _, _ ->
                startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${mainActivity.packageName}")
                    ).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            .show()
    }
}