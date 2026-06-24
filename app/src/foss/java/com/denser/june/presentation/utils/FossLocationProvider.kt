package com.denser.june.presentation.utils

import android.Manifest
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.annotation.RequiresPermission
import com.denser.june.core.domain.model.JournalLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.maplibre.android.geometry.LatLng

class FossLocationProvider : LocationProvider {

    companion object {
        /** Timeout for requesting a fresh GPS fix (milliseconds) */
        private const val GPS_TIMEOUT_MS = 15_000L
        /** Minimum GPS fix age to consider fresh (milliseconds) */
        private const val FRESH_THRESHOLD_MS = 30_000L
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun fetchCurrentLocation(context: Context): JournalLocation? {
        return withContext(Dispatchers.IO) {
            try {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (!gpsEnabled && !networkEnabled) return@withContext null

                // Step 1: Try to get a fresh GPS fix
                var bestLocation: Location? = null

                if (gpsEnabled) {
                    bestLocation = requestFreshGpsFix(locationManager)
                }

                // Step 2: If GPS didn't give a fresh fix, try last known from any provider
                if (bestLocation == null) {
                    bestLocation = getBestLastKnownLocation(locationManager, gpsEnabled, networkEnabled)
                }

                // Step 3: If still nothing, try one more GPS request without timeout
                if (bestLocation == null && gpsEnabled) {
                    bestLocation = requestFreshGpsFix(locationManager)
                }

                bestLocation?.let {
                    MapProviderUtils.updateLocationFromCenter(context, LatLng(it.latitude, it.longitude))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Request a fresh GPS fix with a timeout.
     * Uses coroutine-based LocationListener pattern.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun requestFreshGpsFix(locationManager: LocationManager): Location? {
        return try {
            withTimeout(GPS_TIMEOUT_MS) {
                coroutineScope {
                    val locationFlow = MutableStateFlow<Location?>(null)

                    // Check if a recent fix already exists
                    val lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val now = System.currentTimeMillis()
                    if (lastGps != null && (now - lastGps.time) < FRESH_THRESHOLD_MS) {
                        return@coroutineScope lastGps
                    }

                    val listener = object : LocationListener {
                        override fun onLocationChanged(location: Location) {
                            locationFlow.tryEmit(location)
                        }

                        override fun onProviderEnabled(provider: String) {}
                        override fun onProviderDisabled(provider: String) {}
                        @Deprecated("Deprecated by Android")
                        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    }

                    try {
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null)
                        locationFlow.first { it != null }
                    } finally {
                        locationManager.removeUpdates(listener)
                    }
                }
            }
        } catch (_: Exception) {
            // Timeout or other error - return null to trigger fallback
            null
        }
    }

    /**
     * Get the best available last known location from all enabled providers.
     */
    private fun getBestLastKnownLocation(
        locationManager: LocationManager,
        gpsEnabled: Boolean,
        networkEnabled: Boolean
    ): Location? {
        var best: Location? = null
        val providers = mutableListOf<String>()
        if (gpsEnabled) providers.add(LocationManager.GPS_PROVIDER)
        if (networkEnabled) providers.add(LocationManager.NETWORK_PROVIDER)

        for (provider in providers) {
            val location = locationManager.getLastKnownLocation(provider) ?: continue
            if (best == null || location.accuracy < best.accuracy) {
                best = location
            }
        }
        return best
    }
}
