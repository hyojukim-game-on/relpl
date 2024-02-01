package com.gdd.presentation.base.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task

private const val TAG = "LocationProviderControl_Genseong"
@SuppressLint("MissingPermission")
class LocationProviderController(
    context: Context,
    private val lifecycleOwner: LifecycleOwner
) : DefaultLifecycleObserver {

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val cancellationToken = CancellationTokenSource()

    private var distanceFlag = -1
    private var distancePointFlag = 0.0
    private var beforeLocation: Location? = null

    private var locationListener: (Location?, LocationTrackingException?) -> Unit = { _, _ -> }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult.lastLocation != null) {

                if (beforeLocation == null){
                    beforeLocation = locationResult.lastLocation!!
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
        fusedLocationProviderClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnCompleteListener(completeListener)
    }

    /**
     * @return 이미 추적 중 이라면 false 를 반환
     */
    fun startTrackingLocation(): Boolean {
        return if (distanceFlag < 0) {
            distanceFlag = 0
            fusedLocationProviderClient
                .requestLocationUpdates(
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build(),
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


    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "onStop: ")
        stopTracking()
        cancellationToken.cancel()
    }
}