package com.gdd.presentation.signup

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class SignupViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity){
    private lateinit var viewPagerAdapter: SignupViewPagerAdapter

    private val fragments = listOf<Fragment>(
        SignupPhoneFragment(),
        SignupVerifyFragment(),
        SignupIdFragment(),
        SignupPasswordFragment(),
        SignupNicknameFragment(),
        SignupProfilePhotoFragment()
    )
    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}