package com.gdd.presentation.relay

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gdd.presentation.R
import com.gdd.presentation.databinding.DialogCreateDistanceRelayBinding
import com.gdd.presentation.databinding.DialogCreatePathRelayBinding
import dagger.BindsInstance
import java.text.SimpleDateFormat
import java.util.Calendar

private const val TAG = "CreatePathRelayDialog_Genseong"

class CreatePathRelayDialog(
    clickInterface: CreatePathDialogClickInterface,
    private val distance: Int
) : DialogFragment(){


    private var _binding: DialogCreatePathRelayBinding? = null
    private val binding get() = _binding!!

    private var clickInterface: CreatePathDialogClickInterface? = null

    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일")

    private lateinit var calendar: Calendar

    init {
        this.clickInterface = clickInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCreatePathRelayBinding.inflate(layoutInflater)
        calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, distance/500 + 1)

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvEndDate.text = dateFormatter.format(calendar.timeInMillis)

        // 취소 버튼 클릭
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭
        binding.tvCreate.setOnClickListener {
            this.clickInterface?.onCreateButtonClick(
                binding.etRelayName.editText!!.text.toString(),
                binding.tvEndDate.text.toString()
            )
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        binding.tvReSet.setOnClickListener {
            this.clickInterface?.onReSetButtonClick()
            dismiss()
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface CreatePathDialogClickInterface {
    fun onCreateButtonClick(name: String, endDate: String)

    fun onReSetButtonClick()
}