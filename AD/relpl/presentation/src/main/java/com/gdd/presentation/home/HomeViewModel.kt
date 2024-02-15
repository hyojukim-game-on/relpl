package com.gdd.presentation.home

import androidx.lifecycle.ViewModel
import com.gdd.presentation.base.PrefManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefManager: PrefManager
): ViewModel() {


    fun logout() {
        prefManager.deleteAll()
    }
}