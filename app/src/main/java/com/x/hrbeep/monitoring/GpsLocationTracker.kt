package com.x.hrbeep.monitoring

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

sealed interface GpsTrackingEvent {
    data class LocationUpdate(
        val point: LocationPoint,
    ) : GpsTrackingEvent

    data object ProviderDisabled : GpsTrackingEvent
}

class GpsLocationTracker(
    context: Context,
) {
    private val appContext = context.applicationContext
    private val locationManager = appContext.getSystemService(LocationManager::class.java)

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(
            appContext,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) == PackageManager.PERMISSION_GRANTED

    fun isGpsEnabled(): Boolean = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    fun canTrackDistance(): Boolean = hasLocationPermission() && isGpsEnabled()

    @SuppressLint("MissingPermission")
    fun updates(): Flow<GpsTrackingEvent> = callbackFlow {
        check(hasLocationPermission()) { "Location permission missing." }
        check(isGpsEnabled()) { "GPS is off." }

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                if (location.provider != LocationManager.GPS_PROVIDER) {
                    return
                }

                trySend(
                    GpsTrackingEvent.LocationUpdate(
                        point = LocationPoint(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracyMeters = location.accuracy.takeIf { location.hasAccuracy() },
                        ),
                    ),
                )
            }

            override fun onProviderDisabled(provider: String) {
                if (provider != LocationManager.GPS_PROVIDER) {
                    return
                }

                trySend(GpsTrackingEvent.ProviderDisabled)
                close()
            }
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            LOCATION_UPDATE_INTERVAL_MS,
            0f,
            listener,
            Looper.getMainLooper(),
        )

        awaitClose {
            locationManager.removeUpdates(listener)
        }
    }

    private companion object {
        const val LOCATION_UPDATE_INTERVAL_MS = 1_000L
    }
}
