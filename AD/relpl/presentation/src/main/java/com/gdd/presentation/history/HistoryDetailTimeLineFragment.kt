package com.gdd.presentation.history

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdd.domain.model.relay.RelayDetail
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentHistoryDetailTimeLineBinding
import dagger.hilt.android.AndroidEntryPoint
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo


@AndroidEntryPoint
class HistoryDetailTimeLineFragment : BaseFragment<FragmentHistoryDetailTimeLineBinding>(
    FragmentHistoryDetailTimeLineBinding::bind, R.layout.fragment_history_detail_time_line
) {
    val icon: Drawable? by lazy {
        AppCompatResources.getDrawable(requireContext(), R.drawable.bg_history_detail_indicator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
    }

    private fun initRecyclerView(){
        val detailList = getRelayList()
        binding.rvDetailTimeLine.adapter = HistoryDetailAdapter(
            layoutInflater,
            detailList,
            R.layout.item_history_detail,
        )
        binding.rvDetailTimeLine.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        binding.rvDetailTimeLine.addItemDecoration(getSectionCallbackWithDrawable(detailList))
    }

    /**
     * 서버에서 데이터 들고와야됨
     */
    private fun getRelayList(): List<RelayDetail> = listOf()

    private fun getSectionCallbackWithDrawable(relayList: List<RelayDetail>): SectionCallback {
        return object : SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                relayList[position].moveStart != relayList[position - 1].moveStart

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val relay = relayList[position]
                val dot: Drawable? = icon
                return SectionInfo(relay.moveStart.toFormattedDate(), dotDrawable = dot)
            }

        }
    }

}