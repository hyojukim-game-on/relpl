package com.gdd.presentation.base.location

sealed interface LocationTrackingException{
    class LocationNullException : LocationTrackingException
}
