package com.gdd.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.history.History
import com.gdd.domain.model.history.HistoryInfo
import com.gdd.domain.usecase.history.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase
) : ViewModel() {
    private val _historyResult = MutableLiveData<Result<HistoryInfo>>()
    val historyResult: LiveData<Result<HistoryInfo>>
        get() = _historyResult


    fun loadHistory(userId: Long){
        viewModelScope.launch {
            getHistoryUseCase(userId).let {
                _historyResult.postValue(it)
            }
        }
    }
}