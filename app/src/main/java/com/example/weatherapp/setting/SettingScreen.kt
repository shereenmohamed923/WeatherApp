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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.weatherapp.utility.LocalizationHelper
import com.example.weatherapp.utility.NetworkUtils
import com.example.weatherapp.utility.getFreshLocation
import com.example.weatherapp.utility.isLocationEnabled

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

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExpandableRow(
            title = stringResource(R.string.language),
            options = listOf(stringResource(R.string.english), stringResource(R.string.arabic)),
            initialSelectedOption = if(settingsViewModel.getSavedLanguage() == "en") context.getString(R.string.english) else context.getString(R.string.arabic),
            onOptionSelected = { lang ->
                val langHelper = LocalizationHelper(context)
                val langCode = langHelper.setLanguage(if (lang == context.getString(R.string.english)) "en" else "ar")
                settingsViewModel.saveLanguage(langCode)
            },
            expandedStates = expandedStates,
            key = "language"
        )

        ExpandableRow(
            title = stringResource(R.string.location),
            options = listOf(stringResource(R.string.gps), stringResource(R.string.map)),
            initialSelectedOption =
            if(settingsViewModel.getSavedLocationPreference() == "gps") context.getString(R.string.gps)
            else context.getString(R.string.map),
            onOptionSelected = { option ->
                if (option == context.getString(R.string.gps)) {
                    if(NetworkUtils.isNetworkAvailable(context)){
                        if (isLocationEnabled(context)) {
                            getFreshLocation()
                            settingsViewModel.saveGps()
                            settingsViewModel.saveLocationPreference("gps")
                        } else {
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            context.startActivity(intent)
                        }
                    }else{
                        showDialog = true
                    }
                } else {
                    navController.navigate("location/settings")
                }
            },
            expandedStates = expandedStates,
            key = "location"
        )
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        navController.popBackStack()
                    }) {
                        Text("OK")
                    }
                },
                title = { Text(stringResource(R.string.no_network_connection)) },
                text = { Text(stringResource(R.string.no_network_description)) }
            )
        }

        ExpandableRow(
            title = stringResource(R.string.temperature),
            options = listOf(stringResource(R.string.kelvin_title), stringResource(R.string.celsius_title), stringResource(R.string.fahrenheit_title)),
            initialSelectedOption = settingsViewModel.getSavedTemperatureUnit(),
            onOptionSelected = { unit ->
                if(unit == context.getString(R.string.fahrenheit_title)){
                    settingsViewModel.saveWindSpeedUnit("miles/hour")
                }else{
                    settingsViewModel.saveWindSpeedUnit("meter/second")
                }
                settingsViewModel.saveTemperatureUnit(unit) },
            expandedStates = expandedStates,
            key = "temperature"
        )
    }
}

@Composable
fun ExpandableRow(
    title: String,
    options: List<String>,
    initialSelectedOption: String,
    onOptionSelected: (String) -> Unit,
    expandedStates: MutableMap<String, Boolean>,
    key: String
) {
    var selectedOption by remember { mutableStateOf(initialSelectedOption) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedStates[key] = !(expandedStates[key] ?: false) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expandedStates[key] == true) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = Color.White
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
                            .clickable {
                                selectedOption = option
                                onOptionSelected(option)
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = option, fontSize = 16.sp, color = Color.White)
                        Spacer(modifier = Modifier.weight(1f))
                        if (option == selectedOption) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}