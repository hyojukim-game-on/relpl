package com.gdd.presentation.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.report.GetReportRecordListUseCase
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.model.ReportRecordPoint
import com.gdd.presentation.model.mapper.toReportRecordPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportRecordViewModel @Inject constructor(
    private val getReportRecordListUseCase: GetReportRecordListUseCase,
    private val prefManager: PrefManager
): ViewModel() {
    val nickname = MutableLiveData<String>()

    private var _reportRecordResult = MutableLiveData<Result<List<ReportRecordPoint>>>()
    val reportRecordResult: LiveData<Result<List<ReportRecordPoint>>>
        get() = _reportRecordResult

    init {
        getReportRecord()
    }

    fun getReportRecord(){
        viewModelScope.launch {
            val userId = prefManager.getUserId()
            if (userId != -1L){
                getReportRecordListUseCase(userId).let { result ->
                    _reportRecordResult.postValue(
                        result.map { list ->
                            list.map { it.toReportRecordPoint() }
                        }
                    )
                }
            } else {
                _reportRecordResult.value = Result.failure(Exception("유저 정보 호출에 실패했습니다."))
            }
        }
    }
}