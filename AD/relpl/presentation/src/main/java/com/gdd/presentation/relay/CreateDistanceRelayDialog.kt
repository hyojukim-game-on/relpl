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
import dagger.BindsInstance
import java.text.SimpleDateFormat
import java.util.Calendar

private const val TAG = "CreateDistanceRelayDial_Genseong"

class CreateDistanceRelayDialog(
    dialogClickInterface: DialogClickInterface
) : DialogFragment() {
    private val _createDistanceRelayDist = MutableLiveData<Int>(1000)
    val createDistanceRelayDist: LiveData<Int>
        get() = _createDistanceRelayDist

    private var _binding: DialogCreateDistanceRelayBinding? = null
    private val binding get() = _binding!!

    private var confirmDialogInterface: DialogClickInterface? = null

    val dateFormatter = SimpleDateFormat("yyyy년 MM월 dd일")

    private lateinit var calendar: Calendar

    init {
        this.confirmDialogInterface = dialogClickInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.dialog_create_distance_relay,
            container,
            false
        )

        // 레이아웃 배경을 투명하게 해줌, 필수 아님
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.dialog = this@CreateDistanceRelayDialog
        binding.lifecycleOwner = viewLifecycleOwner

        calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 1)

        binding.tvEndDate.text = dateFormatter.format(calendar.timeInMillis)

        // 취소 버튼 클릭
        binding.tvCancel.setOnClickListener {
            dismiss()
        }

        // 확인 버튼 클릭
        binding.tvCreate.setOnClickListener {
            this.confirmDialogInterface?.onCreateButtonClick(
                binding.etRelayName.editText!!.text.toString(),
                this.createDistanceRelayDist.value!!,
                binding.tvEndDate.text.toString()
            )
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

    private fun plusMeterDist() {
        if (_createDistanceRelayDist.value!! <= 9900) {
            _createDistanceRelayDist.value = _createDistanceRelayDist.value!!.plus(100)

            if ( _createDistanceRelayDist.value!! % 1000 == 0) {
                calendar.add(Calendar.DATE, 1)
                binding.tvEndDate.text = dateFormatter.format(calendar.timeInMillis)
            }
        }
    }

    private fun minusMeterDist() {
        if (_createDistanceRelayDist.value!! > 1000) {
            _createDistanceRelayDist.value = _createDistanceRelayDist.value!!.minus(100)

            if (_createDistanceRelayDist.value!! % 1000 == 900) {
                calendar.add(Calendar.DATE, -1)
                binding.tvEndDate.text = dateFormatter.format(calendar.timeInMillis)
            }
        }
    }

    private fun plusKmDist() {
        if (_createDistanceRelayDist.value!! <= 9000) {
            _createDistanceRelayDist.value = _createDistanceRelayDist.value!!.plus(1000)
            calendar.add(Calendar.DATE, 1)
        }
        binding.tvEndDate.text = dateFormatter.format(calendar.timeInMillis)
    }

    private fun minusKmDist() {
        if (_createDistanceRelayDist.value!! >= 2000) {
            _createDistanceRelayDist.value = _createDistanceRelayDist.value!!.minus(1000)
            calendar.add(Calendar.DATE, -1)
        }
        binding.tvEndDate.text = dateFormatter.format(calendar.timeInMillis)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

interface DialogClickInterface {
    fun onCreateButtonClick(name: String, distance: Int, endDate: String)
}