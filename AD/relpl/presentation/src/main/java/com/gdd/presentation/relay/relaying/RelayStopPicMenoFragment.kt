package com.gdd.presentation.relay.relaying

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.LoadingDialog
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.databinding.FragmentRelayStopPicMenoBinding
import com.gdd.presentation.home.HomeFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class RelayStopPicMenoFragment : BaseFragment<FragmentRelayStopPicMenoBinding>(
    FragmentRelayStopPicMenoBinding::bind, R.layout.fragment_relay_stop_pic_meno
) {
    @Inject
    lateinit var prefManager: PrefManager

    lateinit var mainActivity: MainActivity
    private val mainViewModel: MainViewModel by activityViewModels()
    private val relayStopInfoViewModel: RelayStopInfoViewModel by activityViewModels()

    private var photoFile: File? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        registerListener()
        registerObserve()
    }

    private fun registerListener() {
        binding.ivPloggingImage.setOnClickListener {
            showPicOrGalDialog()
        }

        binding.btnSubmit.setOnClickListener {
            mainActivity.showLoadingView() // 로딩 다이얼로그
            if (photoFile != null){
                stopRelay()
            } else {
                showSnackBar("인증 사진을 반드시 등록해주세요!")
            }
        }
    }

    private fun registerObserve(){
        relayStopInfoViewModel.stopRelayResult.observe(viewLifecycleOwner){
            if (it.isSuccess){
                showToast("릴레이 종료에 성공했습니다.")
                relayStopInfoViewModel.clearRelayingData()
            } else {
                mainActivity.dismissLoadingView()
                showToast(it.exceptionOrNull()?.message ?: "네트워트 에러")
            }
        }

        relayStopInfoViewModel.clearRelayingDataResult.observe(viewLifecycleOwner){
            mainActivity.dismissLoadingView()
            it.getContentIfNotHandled()?.let {
                mainViewModel.reloadUserInfo(prefManager.getUserId())
                parentFragmentManager.popBackStack(HomeFragment.HOME_FRAGMENT_BACKSTACK_NAME,FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }

    private fun showPicOrGalDialog() {
        MaterialAlertDialogBuilder(_activity)
            .setTitle("어느 곳에서 사진을 가져올까요?")
            .setNeutralButton("취소") { _, _ -> }
            .setNegativeButton("갤러리") { _, _ ->
                getImageFromGallery()
            }
            .setPositiveButton("사진찍기") { _, _ ->
                getImageFromPicture()
            }
            .show()
    }


    private fun getRealPathFromURI(uri: Uri): String {
        val buildName = Build.MANUFACTURER
        if (buildName == "Xiomi") return uri.path!!

        var columnIndex = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = _activity.contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }

    private fun getImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        galleryResult.launch(intent)
    }

    private fun getImageFromPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        var photoUri =
            FileProvider.getUriForFile(_activity, "com.gdd.relpl.fileprovider", photoFile!!)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        requestPicture.launch(intent)
    }

    val requestPicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                Glide.with(this)
                    .load(Uri.fromFile(photoFile))
                    .centerCrop()
                    .into(binding.ivPloggingImage)
            }
        }

    private var currnetPhotoPath: String? = null
    private fun createImageFile(): File {
        val timeString = SimpleDateFormat("yyyy-MM-dd HH:mm ").format(System.currentTimeMillis())
        val imageNamePrefix = "relay_stop_pic"
        val storageDir = _activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        return File.createTempFile(
            timeString + imageNamePrefix,
            ".jpg",
            storageDir
        ).apply { currnetPhotoPath = absolutePath }
    }

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imgUri = result.data?.data
                imgUri.let {
                    photoFile = File(getRealPathFromURI(it!!))
                    Glide.with(this)
                        .load(imgUri)
                        .centerCrop()
                        .into(binding.ivPloggingImage)
                }
            }
        }

    /**
     * use after photoFile null check
     */
    private fun stopRelay(){
        try {
            relayStopInfoViewModel.stopRelay(
                mainViewModel.user.id,
                mainViewModel.user.nickname,
                binding.tilMemo.editText!!.text.toString().trim(),
                photoFile!!
            )
        }catch (e: Exception){
            showToast("릴레이 중단 중에 에러가 발생했습니다.")
        }
    }

//    override fun onStop() {
//        super.onStop()
//        mainActivity.dismissLoadingView()
//    }
}