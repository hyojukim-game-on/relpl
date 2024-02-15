package com.gdd.presentation.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.usecase.history.GetHistoryDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val getHistoryDetailUseCase: GetHistoryDetailUseCase
) : ViewModel() {
    private val _historyDetailResult = MutableLiveData<Result<HistoryDetailInfo>>()
    val historyDetailResult: LiveData<Result<HistoryDetailInfo>>
        get() = _historyDetailResult

    fun loadHistoryDetailInfo(projectId: Long){
        viewModelScope.launch {
            getHistoryDetailUseCase(projectId).let {
                _historyDetailResult.postValue(it)
            }
        }
    }
}