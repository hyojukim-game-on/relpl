package com.gdd.presentation.history

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gdd.presentation.MainActivity
import com.gdd.presentation.MainViewModel
import com.gdd.presentation.R
import com.gdd.presentation.base.BaseFragment
import com.gdd.presentation.base.Constants
import com.gdd.presentation.databinding.FragmentHistoryDetailBinding
import com.gdd.retrofit_adapter.RelplException
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryDetailFragment : BaseFragment<FragmentHistoryDetailBinding>(
    FragmentHistoryDetailBinding::bind, R.layout.fragment_history_detail
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val viewModel: HistoryDetailViewModel by viewModels()
    private lateinit var mainActivity: MainActivity

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = _activity as MainActivity
        mainViewModel.loadHistoryDetail(mainViewModel.historySelectedProjectId)

        registerObserver()
    }

    private fun initView(){
        val viewPager = binding.viewpager
        val tabLayout = binding.tabLayout

        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(HistoryDetailTimeLineFragment())
        fragmentList.add(HistoryDetailMapFragment())

        viewPager.adapter = HistoryViewPagerAdapter(fragmentList, mainActivity)
        viewPager.isUserInputEnabled = false

        val iconList = ArrayList<Drawable?>()
        iconList.add(ContextCompat.getDrawable(mainActivity, R.drawable.ic_calendar))
        iconList.add(ContextCompat.getDrawable(mainActivity, R.drawable.ic_map))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.icon = iconList[position]
        }.attach()
    }

    private fun registerObserver(){
        mainViewModel.historyDetailResult.observe(viewLifecycleOwner){ result ->
            if (result != null && result.isSuccess){
                result.getOrNull()?.let {
                    val people = "${Constants.numToKoreanMap[it.projectPeople - it.projectPeople%10]}${Constants.numToKoreanMap[it.projectPeople%10]}"
                    binding.tvTotalPeople.text = people
                    binding.tvTotalDistanceKm.text = (it.projectDistance / 1000).toString()
                    binding.tvTotalDistanceM.text = (it.projectDistance % 1000).toString()
                    val day = it.projectTime / (60 * 24)
                    val hour = (it.projectTime - (60*24*day)) / 60
                    val min = (it.projectTime - (60*24*day)) % 60

                    binding.tvTotalTimeDay.text = day.toString()
                    binding.tvTotalTimeHour.text = hour.toString()
                    binding.tvTotalTimeMin.text = min.toString()
                }
                initView()
            }else{
                result?.exceptionOrNull()?.let {
                    if (it is RelplException){
                        showSnackBar(it.message)
                    } else {
                        showSnackBar(resources.getString(R.string.all_net_err))
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.clearHistoryDetail()
    }
}