package com.gdd.presentation.base.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.Exception

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

    private var locationListener: (Location?, LocationTrackingException?) -> Unit = {_,_ ->}
    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (locationResult.lastLocation != null){
                locationListener(locationResult.lastLocation!!,null)
            } else {
                locationListener(null, LocationTrackingException.LocationNullException())
            }
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }


    fun getCurrnetLocation(
        successListener: (location: Location) -> Unit,
        failureListener: (e: Exception) -> Unit = {}
    ) {
        fusedLocationProviderClient
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener)
    }

    /**
     * @return 이미 추적중이라면 false를 반환
     */
    fun trackingLocation(): Boolean{
        fusedLocationProviderClient
            .requestLocationUpdates(
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build(),
                locationCallback,
                Looper.getMainLooper()
            )
        return true
    }




    override fun onStop(owner: LifecycleOwner) {
        cancellationToken.cancel()
    }
}