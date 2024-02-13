package com.gdd.presentation.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentProfileChangeBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.transition.MaterialContainerTransform
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private const val TAG = "ProfileChangeFragment_Genseong"
@AndroidEntryPoint
class ProfileChangeFragment : BaseFragment<FragmentProfileChangeBinding>(
    FragmentProfileChangeBinding::bind, R.layout.fragment_profile_change
) {
    private val activityViewModel: MainViewModel by activityViewModels()
    private val viewModel: ProfileChangeViewModel by viewModels()
    private lateinit var mainActivity: MainActivity
    private val auth = Firebase.auth

    private var usableNickname = false
    private var usablePhone = false

    private  var profilePhotoFile: File? = null
    private val galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK){
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

    @Inject
    lateinit var prefManager: PrefManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = viewModel

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            scrimColor = Color.TRANSPARENT
        }

        initView()
        registerListener()
        registerObserver()
    }

    private fun initView(){
        viewModel.originalPhone = activityViewModel.user.phone.substring(3)
        viewModel.originNickname = activityViewModel.user.nickname
        Log.d(TAG, "initView: ${viewModel.originNickname}")
        viewModel.setInitialValue(activityViewModel.user.nickname, activityViewModel.user.phone)

        if (activityViewModel.user.imageUri != null){
            Glide.with(this)
                .load(activityViewModel.user.imageUri)
                .fitCenter()
                .apply(RequestOptions().circleCrop())
                .into(binding.ivProfilePhoto)
        }
    }

    private fun registerListener(){
        binding.btnSendCode.setOnClickListener {
            if (!binding.btnPhonneDup.isEnabled){
                sendVerificationCode()
            }else{
                showSnackBar(resources.getString(R.string.profile_change_please_nickname_dup))
            }
        }

        binding.btnVerify.setOnClickListener{
            val credential = PhoneAuthProvider.getCredential(viewModel.verificationId, binding.etVerificationCode.editText!!.text.toString().trim())
            signInWithPhoneAuthCredential(credential)
        }

        binding.btnApply.setOnClickListener{
            //(기존 닉네임과 동일하거나 || 새로운 닉네임이 사용 가능할 때) && (기존 핸드폰 번호와 동일하거나 || 새로운 번호가 유효할 때)
            if (!binding.btnDupCheck.isEnabled &&
                (binding.etPhone.editText!!.text.toString().trim() == viewModel.originalPhone || usablePhone)){
                viewModel.updateProfile(profilePhotoFile,
                    prefManager.getUserId(),
                    binding.etNickname.editText!!.text.toString().trim(),
                    "010${binding.etPhone.editText!!.text.toString().trim()}")
            }else{
                showSnackBar(resources.getString(R.string.all_invalid_input))
            }
        }

        binding.ivProfilePhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.btnDupCheck.setOnClickListener {
            Log.d(TAG, "registerListener: ")
            viewModel.isDuplicatedNickname(binding.etNickname.editText!!.text.toString().trim())
        }

        binding.btnPhonneDup.setOnClickListener {
            viewModel.isDuplicatedPhone("010${binding.etPhone.editText!!.text.toString().trim()}")
        }
    }

    private fun registerObserver(){
        viewModel.newNickname.observe(viewLifecycleOwner){
            usableNickname = false
        }
        viewModel.newPhone.observe(viewLifecycleOwner){
            usablePhone = false
        }


        viewModel.nicknameDupResult.observe(viewLifecycleOwner){ result ->
            binding.etNickname.isEnabled = true
            if (result.isSuccess){
                if (!result.getOrNull()!!){
                    binding.btnDupCheck.isEnabled = false
                    showSnackBar(resources.getString(R.string.profile_change_usable_nickname))
                    usableNickname = true
                }else{
                    showSnackBar(resources.getString(R.string.profile_change_dup_nickname))
                    binding.btnDupCheck.isEnabled = true
                    usableNickname = false
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showSnackBar(resources.getString(R.string.all_net_err))
                    }
                }
                binding.btnDupCheck.isEnabled = true
                usableNickname = false
            }
        }

        viewModel.phoneDupResult.observe(viewLifecycleOwner){ result ->
            binding.etPhone.isEnabled = true
            if (result.isSuccess){
                if (!result.getOrNull()!!){
                    binding.btnPhonneDup.isEnabled = false
                    showSnackBar(resources.getString(R.string.profile_change_usable_phone))
                }else{
                    showToast(resources.getString(R.string.profile_change_dup_phone))
                    binding.btnDupCheck.isEnabled = true
                    usablePhone = false
                }
            }else{
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast(resources.getString(R.string.all_net_err))
                    }
                }
                binding.btnDupCheck.isEnabled = true
                usablePhone = false
            }
        }

        viewModel.profileChangeResult.observe(viewLifecycleOwner){ result ->
            if (result.isSuccess){
                //mainViewModel에서 정보 다시 load
                activityViewModel.reloadUserInfo(prefManager.getUserId())
                parentFragmentManager.popBackStack()
            }else{
                result.exceptionOrNull()?.let {
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
        val cursor = mainActivity.contentResolver.query(uri, proj, null, null, null)
        if (cursor!!.moveToFirst()){
            columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        }
        val result = cursor.getString(columnIndex)
        cursor.close()
        return result
    }


    private fun sendVerificationCode(){
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) { }
            override fun onVerificationFailed(e: FirebaseException) {
                showToast(resources.getString(R.string.all_verify_send_fail))
                binding.etPhone.editText?.isEnabled = true
            }
            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                viewModel.verificationId = verificationId
                showSnackBar(resources.getString(R.string.all_verify_send_success))
            }
        }
        val phoneNumber = "+82010${binding.etPhone.editText!!.text.trim()}"

        val optionsCompat =  PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(90L, TimeUnit.SECONDS)
            .setActivity(mainActivity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(optionsCompat)
        auth.setLanguageCode("kr")
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(mainActivity) { task ->
                usablePhone = if (task.isSuccessful) {
                    showSnackBar(resources.getString(R.string.all_verify_success))
                    true
                } else {
                    //인증실패
                    showSnackBar(resources.getString(R.string.all_verify_fail))
                    false
                }
            }
    }
//    fun isDuplicatedNickname(view: View){
//        binding.etNickname.isEnabled = false
//        binding.btnDupCheck.isEnabled = false
//        viewModel.isDuplicatedNickname(binding.etNickname.editText!!.text.toString().trim())
//    }
//
//    fun isDuplicatedPhone(view: View){
//        binding.etPhone.isEnabled = false
//        binding.btnPhonneDup.isEnabled = false
//        viewModel.isDuplicatedPhone(binding.etPhone.editText!!.text.toString().trim())
//    }

    companion object{
        const val REQ_GALLERY = 10001
    }
}