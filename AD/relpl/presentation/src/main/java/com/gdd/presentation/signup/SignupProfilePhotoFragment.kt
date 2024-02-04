package com.gdd.presentation.signup

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdd.presentation.R
import com.gdd.presentation.SignupActivity
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentSignupProfilePhotoBinding
import com.gdd.retrofit_adapter.RelplException
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.jar.Manifest
import kotlin.math.log

private const val TAG = "SignupProfilePhotoFragm_Genseong"
@AndroidEntryPoint
class SignupProfilePhotoFragment : BaseFragment<FragmentSignupProfilePhotoBinding>(
    FragmentSignupProfilePhotoBinding::bind, R.layout.fragment_signup_profile_photo
) {
    private lateinit var signUpActivity: SignupActivity
    private val activityViewModel: SignupViewModel by activityViewModels()
    private lateinit var profilePhotoFile: File
    private var userId = -1L
    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == RESULT_OK){
            val imgUri = result.data?.data
            imgUri.let {
                profilePhotoFile = File(getRealPathFromURI(it!!))
                Glide.with(this)
                    .load(imgUri)
                    .fitCenter()
                    .apply(RequestOptions().circleCrop())
                    .into(binding.ivProfilePhoto)

            }
        }
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
                galleryResult.launch(intent)
            }else
                Log.d(TAG, "deny")
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpActivity = _activity as SignupActivity

        registerListener()
        registerObserver()
    }

    private fun registerListener(){
        binding.ivProfilePhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.btnDone.setOnClickListener {
            if(profilePhotoFile.exists()){
                Log.d(TAG, "registerListener: true")
                activityViewModel.registerProfilePhoto(profilePhotoFile, userId)
            }else{
                Log.d(TAG, "registerListener: false")
                showSnackBar(resources.getString(R.string.signup_sucess))
                signUpActivity.finish()
            }
        }
    }

    private fun registerObserver(){
        activityViewModel.signUpResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                result.getOrNull()?.let {
                    binding.tvWelcome.text = resources.getString(R.string.signup_photo_welcome, it.nickname)
                    userId = it.id
                }
            }
        }

        activityViewModel.registerPhotoResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                showSnackBar(resources.getString(R.string.signup_sucess))
                signUpActivity.finish()
            }else{
                result.exceptionOrNull()?.let {
                    Log.d(TAG, "registerObserver: ${it.toString()}")
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }
    }

    private fun getRealPathFromURI(uri: Uri): String{
        val buildName = Build.MANUFACTURER
        if (buildName == "Xiomi") return uri.path!!

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = signUpActivity.contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()){
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }

    companion object{
        const val REQ_GALLERY = 10001
    }
}