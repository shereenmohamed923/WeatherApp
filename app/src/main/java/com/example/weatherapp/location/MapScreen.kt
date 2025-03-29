package com.example.weatherapp.location

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.repo.LocationRepositoryImpl
import com.example.weatherapp.data.repo.SettingRepositoryImpl
import com.example.weatherapp.setting.SettingFactory
import com.example.weatherapp.setting.SettingViewModel
import com.example.weatherapp.utility.getAddressFromLocation
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(navController: NavHostController) {
    val context = LocalContext.current

    val settingsViewModel: SettingViewModel = viewModel(
        factory = SettingFactory(
            SettingRepositoryImpl.getInstance(context),
            LocationRepositoryImpl.getInstance(context)
        )
    )

    val savedLatLng = if (settingsViewModel.getSavedLocation() == Coord(0.0, 0.0)) LatLng(30.0444, 31.2357)
    else LatLng(settingsViewModel.getSavedLocation().lat, settingsViewModel.getSavedLocation().lon)

    var selectedLocation by remember { mutableStateOf(savedLatLng) }
    var address by remember { mutableStateOf("Select a location") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(savedLatLng, 12f)
    }

    Box(
        contentAlignment = Alignment.BottomStart
    )  {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                selectedLocation = latLng
                address = getAddressFromLocation(context, latLng.latitude, latLng.longitude)
            }
        ) {
            Marker(
                state = MarkerState(position = selectedLocation),
                title = "Selected Location"
            )
        }
        Column(
            modifier = Modifier.height(150.dp)
                .fillMaxWidth()
                .background(brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xCC352163),
                        Color(0xCC331972),
                        Color(0xCC33143C)
                    )
                ))
        ) {
            Text(
                text = address,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                color = Color.White
            )

            Button(
                onClick = {
                    Log.i("TAG", "MapScreen: ${selectedLocation.latitude}, ${selectedLocation.longitude}")
                    settingsViewModel.saveLocation(
                        lat = selectedLocation.latitude,
                        lon = selectedLocation.longitude
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Save Location")
            }
        }


    }
}