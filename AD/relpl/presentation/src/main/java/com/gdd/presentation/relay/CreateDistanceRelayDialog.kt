package com.gdd.presentation.relay

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.gdd.presentation.R
import com.gdd.presentation.databinding.DialogCreateDistanceRelayBinding
import dagger.BindsInstance

class CreateDistanceRelayDialog(
    dialogClickInterface: DialogClickInterface
) : DialogFragment(){
    private val _createDistanceRelayDist = MutableLiveData<Int>(1000)
    val createDistanceRelayDist : LiveData<Int>
        get() = _createDistanceRelayDist

    private var _binding: DialogCreateDistanceRelayBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: DialogClickInterface? = null


    init {
        this.confirmDialogInterface = confirmDialogInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.dialog_create_distance_relay, container, false)

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 취소 버튼 클릭
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭
        binding.tvCreate.setOnClickListener {
            this.confirmDialogInterface?.onCreateButtonClick(binding.etRelayName.editText!!.text.toString())
            dismiss()
        }

        binding.btnKmPlus.setOnClickListener {
            plusKmDist()
        }

        binding.btnKmMinus.setOnClickListener {
            minusKmDist()
        }

        binding.btnMeterPlus.setOnClickListener {
            plusMeterDist()
        }

        binding.btnMeterMinus.setOnClickListener {
            minusMeterDist()
        }



        return binding.root
    }

    fun plusMeterDist(){
        _createDistanceRelayDist.value!!.plus(100)
    }

    fun minusMeterDist(){
        if (_createDistanceRelayDist.value!! > 1000)
            _createDistanceRelayDist.value!!.minus(100)
    }

    fun plusKmDist(){
        _createDistanceRelayDist.value!!.plus(1000)
    }

    fun minusKmDist(){
        if (_createDistanceRelayDist.value!! > 2000)
            _createDistanceRelayDist.value!!.minus(1000)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface DialogClickInterface {
    fun onCreateButtonClick(name: String)
}