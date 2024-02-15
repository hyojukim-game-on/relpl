package com.gdd.presentation.base.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "LocationProviderControl_Genseong"
@SuppressLint("MissingPermission")
class LocationProviderController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) : DefaultLifecycleObserver {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private var cancellationToken = CancellationTokenSource()

    private var distanceFlag = -1
    private var beforeLocation: Location? = null

    private var locationListener: (Location?, LocationTrackingException?) -> Unit = { _, _ -> }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            Log.d(TAG, "onLocationResult: $locationResult")
            if (locationResult.lastLocation != null) {
                
                if (beforeLocation == null){
                    beforeLocation = locationResult.lastLocation!!
                    locationListener(locationResult.lastLocation!!, null)
                } else if (beforeLocation!!.distanceTo(locationResult.lastLocation!!) >= distanceFlag) {
                    beforeLocation = locationResult.lastLocation!!
                    locationListener(locationResult.lastLocation!!, null)
                }

            } else {
                locationListener(null, LocationTrackingException.LocationNullException())
            }
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }


    fun getCurrnetLocation(
        completeListener: (task: Task<Location>) -> Unit
    ) {
        scope.launch {
            fusedLocationProviderClient
                .getCurrentLocation(currentLocationRequest, cancellationToken.token)
                .addOnCompleteListener(completeListener)
        }
    }

    /**
     * @param distance meter 단위로 입력, 입력값 이상의 이동이 일어날 때 리스너가 동작합니다.
     * @return 추적 시작시 true, 이미 추적 중 이라면 false 를 반환
     */
    fun startTrackingLocation(distance: Int ,listener:(Location?, LocationTrackingException?) -> Unit): Boolean {
        return if (distanceFlag < 0) {
            distanceFlag = distance
            locationListener = listener
            fusedLocationProviderClient
                .requestLocationUpdates(
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 500).build(),
                    locationCallback,
                    Looper.getMainLooper()
                )
            true
        } else {
            false
        }
    }

    fun stopTracking() {
        if (distanceFlag >= 0) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            distanceFlag = -1
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        cancellationToken = CancellationTokenSource()
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "onStop: ")
        stopTracking()
        cancellationToken.cancel()
    }

    private val currentLocationRequest =
        CurrentLocationRequest.Builder()
            .setDurationMillis(Long.MAX_VALUE)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .setMaxUpdateAgeMillis(1000)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()
}