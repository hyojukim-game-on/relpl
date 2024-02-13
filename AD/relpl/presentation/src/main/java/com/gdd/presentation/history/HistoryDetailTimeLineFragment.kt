package com.gdd.presentation.history

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdd.domain.model.history.HistoryDetail
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentHistoryDetailTimeLineBinding
import com.gdd.presentation.mapper.DateFormatter
import com.gdd.retrofit_adapter.RelplException
import dagger.hilt.android.AndroidEntryPoint
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo


private const val TAG = "HistoryDetailTimeLineFragment_GenSeong"
@AndroidEntryPoint
class HistoryDetailTimeLineFragment : BaseFragment<FragmentHistoryDetailTimeLineBinding>(
    FragmentHistoryDetailTimeLineBinding::bind, R.layout.fragment_history_detail_time_line
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var historyDetailList: List<HistoryDetail>

    val icon: Drawable? by lazy {
        AppCompatResources.getDrawable(requireContext(), R.drawable.bg_history_detail_indicator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: 생성!")

//        initRecyclerView()
        registerObserver()
    }

    private fun registerObserver(){
        mainViewModel.historyDetailResult.observe(viewLifecycleOwner){ result ->
            if (result != null){
                if (result.isSuccess){
                    result.getOrNull()?.let {
                        historyDetailList = it.detailList
                        initRecyclerView()
                    }
                }else{
                    result.exceptionOrNull()?.let {
                        if (it is RelplException){
                            showSnackBar(it.message)
                        } else {
                            showSnackBar(resources.getString(R.string.all_net_err))
                        }
                    }
                }
            }
        }
    }

    private fun initRecyclerView(){
        binding.rvDetailTimeLine.adapter = HistoryDetailAdapter(
            requireContext(),
            layoutInflater,
            historyDetailList,
            R.layout.item_history_detail,
        )
        binding.rvDetailTimeLine.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        binding.rvDetailTimeLine.addItemDecoration(getSectionCallbackWithDrawable(historyDetailList))
    }

    private fun getSectionCallbackWithDrawable(historyDetailList: List<HistoryDetail>): SectionCallback {
        return object : SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                DateFormatter.longToHistoryFormat(historyDetailList[position].moveStart.substring(0, 10)) != DateFormatter.longToHistoryFormat(historyDetailList[position - 1].moveStart.substring(0, 10))

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val relay = historyDetailList[position]
                val dot: Drawable? = icon
                return SectionInfo(DateFormatter.longToHistoryFormat(relay.moveStart), dotDrawable = dot)
            }

        }
    }

}