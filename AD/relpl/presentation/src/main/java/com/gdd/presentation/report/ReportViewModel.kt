package com.gdd.presentation.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.usecase.report.RegistReportUseCase
import com.gdd.presentation.base.PrefManager
import com.gdd.presentation.mapper.DateFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val registReportUseCase: RegistReportUseCase,
    private val prefManager: PrefManager
): ViewModel() {
    private var _registReportResult = MutableLiveData<Result<Boolean>>()
    val registReportResult: LiveData<Result<Boolean>>
        get() = _registReportResult

    fun registReport(latitude: Double, longitude: Double){
        val userId = prefManager.getUserId()
        if (userId != -1L){
            viewModelScope.launch {
                registReportUseCase(
                    userId,
                    DateFormatter.getReportDateFormatString(System.currentTimeMillis()),
                    latitude, longitude
                ).let {
                    _registReportResult.postValue(it)
                }
            }
        } else {
            _registReportResult.value = Result.failure(Exception("유저 정보 호출에 실패했습니다."))
        }
    }
}