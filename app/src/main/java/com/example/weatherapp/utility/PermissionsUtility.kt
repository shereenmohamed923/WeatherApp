package com.example.weatherapp.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.example.weatherapp.data.model.Coord
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

lateinit var fusedClient : FusedLocationProviderClient
var addressState = mutableStateOf<String?>("")
var _locationState = MutableStateFlow<Location?>(null)

fun checkPermissions(context: Context): Boolean{
    return ContextCompat.checkSelfPermission(
        context, android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

}

@SuppressLint("MissingPermission")
fun getFreshLocation(){
    val locationRequest = LocationRequest.Builder(0).apply {
        setPriority(Priority.PRIORITY_HIGH_ACCURACY)
    }.build()
    fusedClient.requestLocationUpdates(
        locationRequest,
        object : LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    Log.i("TAG", "onLocationResult: ${location.latitude}, ${location.longitude}")
                    _locationState.value = location
                }
            }
        },
        Looper.myLooper()
    )
}

 fun isLocationEnabled(context: Context): Boolean{
    val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}

fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            addresses[0].getAddressLine(0)
        } else {
            "Address not found"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Unable to get address"
    }
}