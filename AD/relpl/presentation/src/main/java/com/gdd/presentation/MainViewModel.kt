package com.gdd.presentation

import androidx.lifecycle.ViewModel
import com.gdd.domain.model.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

): ViewModel() {
    lateinit var user: User
}