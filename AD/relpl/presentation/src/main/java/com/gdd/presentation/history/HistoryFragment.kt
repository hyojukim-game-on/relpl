package com.gdd.presentation.history

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gdd.domain.model.history.History
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.databinding.FragmentHistoryBinding
import com.gdd.retrofit_adapter.RelplException
import dagger.hilt.android.AndroidEntryPoint
import xyz.sangcomz.stickytimelineview.callback.SectionCallback
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import javax.inject.Inject


@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(
    FragmentHistoryBinding::bind, R.layout.fragment_history
) {
    private val viewModel: HistoryViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var mainActivity: MainActivity
    private lateinit var historyList: List<History>

    @Inject
    lateinit var prefManager: PrefManager

    val icon: Drawable? by lazy {
        AppCompatResources.getDrawable(requireContext(), R.drawable.bg_history_indicator)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.loadHistory(prefManager.getUserId())

        if (mainViewModel.user.imageUri != null) {
            Glide.with(this)
                .load(mainViewModel.user.imageUri)
                .fitCenter()
                .apply(RequestOptions().circleCrop())
                .into(binding.ivProfilePhoto)
        }

        binding.tvUserNickname.text =
            resources.getString(R.string.history_user_nickname, mainViewModel.user.nickname)
        // 프로필 이미지 띄우기

        registerListener()
        registerObserver()
    }

    private fun registerListener() {

    }

    private fun registerObserver() {
        viewModel.historyResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                result.getOrNull()?.let {

                    if (it.totalProject == 0) {
                        binding.tvNoDataSummery.visibility = View.VISIBLE
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.llInfoSummery.visibility = View.GONE
                    } else {
                        binding.tvNoDataSummery.visibility = View.GONE
                        binding.tvNoData.visibility = View.GONE

                        binding.tvTotalProject.text = it.totalProject.toString()
                        binding.tvTotalDistanceKm.text = (it.userTotalDistance / 1000).toString()
                        binding.tvTotalDistanceM.text = (it.userTotalDistance % 1000).toString()
                        val day = it.userTotalTime / (60 * 24)
                        val hour = (it.userTotalTime - (60 * 24 * day)) / 60
                        val min = (it.userTotalTime - (60 * 24 * day)) % 60

                        binding.tvTotalTimeDay.text = day.toString()
                        binding.tvTotalTimeHour.text = hour.toString()
                        binding.tvTotalTimeMin.text = min.toString()

                        historyList = it.detailList.sorted().reversed()
                        initRecyclerView()
                    }
                }
            } else {
                result.exceptionOrNull()?.let {
                    if (it is RelplException) {
                        showSnackBar(it.message)
                    } else {
                        showSnackBar(resources.getString(R.string.all_net_err))
                        binding.tvLoadFail.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.rvTimeLine.adapter = HistoryAdapter(
            layoutInflater,
            historyList,
            R.layout.item_history,
            relayClickListener
        )
        binding.rvTimeLine.layoutManager = LinearLayoutManager(
            mainActivity,
            RecyclerView.VERTICAL,
            false
        )
        binding.rvTimeLine.addItemDecoration(getSectionCallbackWithDrawable(historyList))
    }


    private fun getSectionCallbackWithDrawable(historyList: List<History>): SectionCallback {
        return object : SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean =
                historyList[position].createDate != historyList[position - 1].createDate

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? {
                val relay = historyList[position]
                val dot: Drawable? = icon
                return SectionInfo(relay.createDate.toFormattedDate(), dotDrawable = dot)
            }

        }
    }

    private var relayClickListener: (History) -> Unit = { history ->
        mainViewModel.historySelectedProjectId = history.projectId
        parentFragmentManager.beginTransaction()
            .replace(R.id.layout_main_fragment, HistoryDetailFragment()).addToBackStack(null)
            .commit()

    }
}