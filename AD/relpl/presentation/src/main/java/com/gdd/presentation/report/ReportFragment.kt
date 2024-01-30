package com.gdd.presentation.report

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.gdd.presentation.MainActivity
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.PermissionHelper
import com.gdd.presentation.databinding.FragmentReportBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "ReportFragment_Genseong"

class ReportFragment : BaseFragment<FragmentReportBinding>(
    FragmentReportBinding::bind, R.layout.fragment_report
) {
    private lateinit var mainActivity: MainActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity

        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (!PermissionHelper.checkPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
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