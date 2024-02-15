package com.gdd.presentation.point

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.domain.model.point.PointRecordListItem
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentPointRecordBinding
import com.gdd.retrofit_adapter.RelplException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PointRecordFragment : BaseFragment<FragmentPointRecordBinding>(
    FragmentPointRecordBinding::bind, R.layout.fragment_point_record
) {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val pointRecordViewModel: PointRecordViewModel by viewModels()
    private var pointRecordListAdapter: PointRecordListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            pointRecordViewModel.setNickname(mainViewModel.user.nickname)
        } catch (t: Throwable) {
            showToast("회원 정보 호출에 실패했습니다.")
        }

        binding.lifecycleOwner = viewLifecycleOwner
        binding.pointRecordViewModel = pointRecordViewModel

        registerObserve()
        pointRecordViewModel.getPointRecord()
    }

    private fun registerObserve() {
        pointRecordViewModel.pointRecordList.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess){
                result.getOrDefault(listOf()).let {
                    setAdapter(it)
                }
            } else {
                result.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showToast("네트워크 오류")
                    }
                }
            }
        }
    }

    private fun setAdapter(list: List<PointRecordListItem>){
        if (binding.rvPointRecord.adapter == null) {
            if (pointRecordListAdapter == null){
                pointRecordListAdapter = PointRecordListAdapter(list)
            }
            binding.rvPointRecord.adapter = pointRecordListAdapter
        }
    }
}