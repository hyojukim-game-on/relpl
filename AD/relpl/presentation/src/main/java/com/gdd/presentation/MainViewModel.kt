package com.gdd.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.history.HistoryDetailInfo
import com.gdd.domain.model.user.User
import com.gdd.domain.usecase.history.GetHistoryDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getHistoryDetailUseCase: GetHistoryDetailUseCase
): ViewModel() {
    lateinit var user: User
    var historySelectedProjectId = -1L

    private val _historyDetailResult = MutableLiveData<Result<HistoryDetailInfo>?>()
    val historyDetailResult: LiveData<Result<HistoryDetailInfo>?>
        get() = _historyDetailResult

    fun loadHistoryDetail(projectId: Long){
        viewModelScope.launch {
            getHistoryDetailUseCase(projectId).let {
                _historyDetailResult.postValue(it)
            }
        }
    }

    fun clearHistoryDetail(){
        _historyDetailResult.postValue(null)
    }
}