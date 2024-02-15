package com.gdd.presentation.profile

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gdd.presentation.R
import com.gdd.presentation.databinding.DialogCreateDistanceRelayBinding
import com.gdd.presentation.databinding.DialogExitBinding
import dagger.BindsInstance
import java.text.SimpleDateFormat
import java.util.Calendar

private const val TAG = "ExitDialog_Genseong"

class CreateDistanceRelayDialog(
    dialogClickInterface: DialogClickInterface
) : DialogFragment(){

    private var _binding: DialogExitBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: DialogClickInterface? = null

    init {
        this.confirmDialogInterface = dialogClickInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogExitBinding.inflate(layoutInflater)

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 취소 버튼 클릭
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭
        binding.tvExit.setOnClickListener {
            if (binding.etPw.isNotEmpty()){
                this.confirmDialogInterface?.onExitButtonClick(
                    binding.etPw.editText!!.text.toString().trim()
                )
                dismiss()
            }else{
                Toast.makeText(requireContext(), resources.getString(R.string.all_input_everything), Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface DialogClickInterface {
    fun onExitButtonClick(password: String)
}