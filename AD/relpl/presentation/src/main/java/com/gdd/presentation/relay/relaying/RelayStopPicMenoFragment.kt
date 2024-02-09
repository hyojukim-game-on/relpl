package com.gdd.presentation.relay.relaying

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentRelayStopPicMenoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class RelayStopPicMenoFragment : BaseFragment<FragmentRelayStopPicMenoBinding>(
    FragmentRelayStopPicMenoBinding::bind, R.layout.fragment_relay_stop_pic_meno
) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val relayStopInfoViewModel: RelayStopInfoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerListener()
    }

    private fun registerListener() {
        binding.ivPloggingImage.setOnClickListener {
            showPicOrGalDialog()
        }
    }

    private fun showPicOrGalDialog() {
        MaterialAlertDialogBuilder(_activity)
            .setNeutralButton("취소") { _, _ -> }
            .setNegativeButton("갤러리") { _, _ ->
                getImageFromGallery()
            }
            .setPositiveButton("사진찍기") { _, _ ->
                getImageFromPicture()
            }
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

    }


    private lateinit var profilePhotoFile: File
    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imgUri = result.data?.data
                imgUri.let {
                    profilePhotoFile = File(getRealPathFromURI(it!!))
                    Glide.with(this)
                        .load(imgUri)
                        .fitCenter()
                        .apply(RequestOptions().circleCrop())
                        .into(binding.ivPloggingImage)
                }
            }
        }
}