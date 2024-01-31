package com.gdd.presentation.relay

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoadRelayViewModel @Inject constructor(

) : ViewModel() {
    private val _markerResult = MutableLiveData<List<LatLng>>()
    val markerResult: LiveData<List<LatLng>>
        get() = _markerResult

    fun loadMarker(){
        _markerResult.postValue(tempMarkerList)
    }
    companion object{
        val tempMarkerList = arrayListOf<LatLng>(
            LatLng(36.108899, 128.418671),
            LatLng(36.108540, 128.420353),
            LatLng(36.107000, 128.422437),
            LatLng(36.106996,  128.422379),
            LatLng(36.105022, 128.422064),
            LatLng(36.104238, 128.419391),
            LatLng(36.104024, 128.418710),
            LatLng(36.101772, 128.420475),
            LatLng(36.101640, 128.421811),
            LatLng(36.100745, 128.420818),
            LatLng(36.104214, 128.425846)
        )
    }
}