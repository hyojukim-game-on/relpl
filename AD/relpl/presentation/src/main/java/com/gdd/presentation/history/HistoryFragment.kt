package com.gdd.presentation.history

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gdd.domain.model.relay.Relay
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo


@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(
    FragmentHistoryBinding::bind, R.layout.fragment_history
) {
    private val viewModel: HistoryViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity

    val icon: Drawable? by lazy {
        AppCompatResources.getDrawable(requireContext(), R.drawable.bg_history_indicator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        binding.lifecycleOwner = viewLifecycleOwner

        initRecyclerView()
    }

    private fun initRecyclerView(){
        val relayList = getRelayList()
        binding.rvTimeLine.adapter = HistoryAdapter(
            layoutInflater,
            relayList,
            R.layout.item_history,
            relayClickListener
        )
        binding.rvTimeLine.layoutManager = LinearLayoutManager(
            mainActivity,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvTimeLine.addItemDecoration(getSectionCallbackWithDrawable(relayList))
    }

    /**
     * 네트워크에서 데이터 가져오기
     */
    //private fun getRelayList(): List<Relay> = RelayRepo().relayList
    private fun getRelayList(): List<Relay> = listOf()

    private fun getSectionCallbackWithDrawable(relayList: List<Relay>): SectionCallback {
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

    private var relayClickListener : (Relay) -> Unit = { relay ->
        if (!relay.projectIsdone){
            showSnackBar("완료 되지 않은 릴레이입니다")
        }else{
//            val intent = Intent(this, DetailActivity::class.java)
//            startActivity(intent)
            /**
             * 디테일 프래그먼트로 넘어가기
             */
        }
    }
}