package com.gdd.presentation.base

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

private const val TAG = "PermissionHelper_Genseong"
object PermissionHelper {

    private val PERMISSION_REQUEST_CODE = 20000

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

    /**
     * @param grantedListener 는 주어진 권한이 전부 허용됐을때 동작합니다.
     * @param deniedListener 주어진 권한중 하나라도 거절 되었을때 동작합니다.
     */
    fun requestPermissionList_fragment(fragment: Fragment, permissions: Array<String>, grantedListener: ()->Unit = {}, deniedListener: ()->Unit = {}){
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){ permission ->
            if(permission.all { it.value }){
                grantedListener()
            } else {
                deniedListener()
            }
        }.launch(permissions)
    }


}