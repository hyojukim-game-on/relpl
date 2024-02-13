package com.gdd.presentation.rank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gdd.domain.model.rank.Rank
import com.gdd.domain.usecase.rank.GetRankUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(
    private val getRankUseCase: GetRankUseCase
): ViewModel() {
    private var _rankResult = MutableLiveData<Result<Rank>>()
    val rankResult: LiveData<Result<Rank>>
        get() = _rankResult

    init {
        getRank()
    }


    fun getRank(){
        viewModelScope.launch {
            getRankUseCase().let {
                _rankResult.postValue(it)
            }
        }
    }
}