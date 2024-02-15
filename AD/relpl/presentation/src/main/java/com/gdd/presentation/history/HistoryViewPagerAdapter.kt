package com.gdd.presentation.history

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HistoryViewPagerAdapter(
    private val fragmentList: ArrayList<Fragment>,
    container: AppCompatActivity
): FragmentStateAdapter(container.supportFragmentManager, container.lifecycle) {
    override fun getItemCount(): Int = fragmentList.count()
    override fun createFragment(position: Int): Fragment = fragmentList[position]
}