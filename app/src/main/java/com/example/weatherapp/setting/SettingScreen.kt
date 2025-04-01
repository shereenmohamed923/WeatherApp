package com.example.weatherapp.setting

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.data.repo.LocationRepositoryImpl
import com.example.weatherapp.data.repo.SettingRepositoryImpl
import com.example.weatherapp.location.MapScreen
import com.example.weatherapp.utility.LocalizationHelper
import com.example.weatherapp.utility.getFreshLocation
import com.example.weatherapp.utility.isLocationEnabled
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

@Composable
fun SettingScreen(navController: NavController) {
    val expandedStates = remember {
        mutableStateMapOf(
            "language" to false,
            "location" to false,
            "temperature" to false,
            "wind" to false
        )
    }
    val context = LocalContext.current

    val settingsViewModel: SettingViewModel = viewModel(
        factory = SettingFactory(
            SettingRepositoryImpl.getInstance(context),
            LocationRepositoryImpl.getInstance(context)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExpandableRow(
            title = stringResource(R.string.language),
            options = listOf("English", "Arabic"),
            selectedOption = settingsViewModel.getSavedLanguage(),
            onOptionSelected = { lang ->
                val langHelper = LocalizationHelper(context)
                val langCode = langHelper.setLanguage(if (lang == "English") "en" else "ar")
                settingsViewModel.saveLanguage(langCode)
            },
            expandedStates = expandedStates,
            key = "language"
        )

        ExpandableRow(
            title = stringResource(R.string.location),
            options = listOf("GPS", "Map"),
            selectedOption = "",
            onOptionSelected = { option ->
                if (option == "GPS") {
                    if (isLocationEnabled(context)) {
                        getFreshLocation()
                        settingsViewModel.saveGps()
                    } else {
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }
                } else {
                    navController.navigate("location")
                }
            },
            expandedStates = expandedStates,
            key = "location"
        )

        ExpandableRow(
            title = stringResource(R.string.temperature),
            options = listOf("Kelvin", "Celsius", "Fahrenheit"),
            selectedOption = settingsViewModel.getTemperatureUnit(),
            onOptionSelected = { unit -> settingsViewModel.setTemperatureUnit(unit) },
            expandedStates = expandedStates,
            key = "temperature"
        )

        // Wind Speed Unit Selection
//        ExpandableRow(
//            title = stringResource(R.string.wind_speed),
//            options = listOf("meter/sec", "miles/hour"),
//            selectedOption = viewModel.getSavedWindSpeedUnit(),
//            onOptionSelected = { unit -> viewModel.saveWindSpeedUnit(unit) },
//            expandedStates = expandedStates,
//            key = "wind"
//        )
    }
}

@Composable
fun ExpandableRow(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    expandedStates: MutableMap<String, Boolean>,
    key: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedStates[key] = !(expandedStates[key] ?: false) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expandedStates[key] == true) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand"
            )
        }

        if (expandedStates[key] == true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray.copy(alpha = 0.2f))
                    .padding(8.dp)
            ) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option) }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = option, fontSize = 16.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        if (option == selectedOption) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.Blue
                            )
                        }
                    }
                }
            }
        }
    }
}