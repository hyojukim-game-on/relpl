package com.gdd.presentation.base

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

object PermissionHelper {

    /**
     * @param permission use Manifest.permission.*
     * @return if param Permission is Granted return true else false
     */
    fun checkPermission(context: Context, permission: String): Boolean{
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * @param permissionList use listOf(Manifest.permission.*, ... , ...)
     * @return return denied permissions
     */
    fun checkPermissionList(context: Context, permissionList: List<String>): List<String>{
        return permissionList.filter {
            context.checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
        }
    }

    /**
     * @param fragment fragment that call this
     * @param permission use Manifest.permission.*
     */
    fun requestPermission_fragment(fragment: Fragment, permission: String, grantedListener: ()->Unit = {}, deniedListener: ()->Unit = {}){
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) grantedListener()
            else deniedListener()
        }.launch(permission)
    }
}