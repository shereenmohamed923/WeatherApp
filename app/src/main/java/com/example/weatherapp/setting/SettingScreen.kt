package com.example.weatherapp.setting

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
import com.example.weatherapp.R
import com.example.weatherapp.utility.LocalizationHelper

@Composable
fun SettingScreen(viewModel: SettingViewModel) {
    val expandedStates = remember { mutableStateMapOf("language" to false, "location" to false, "temperature" to false, "wind" to false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.settings), fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        ExpandableRow(
            title = stringResource(R.string.language),
            options = listOf("English", "Arabic"),
            selectedOption = viewModel.getSavedLanguage(),
            onOptionSelected = { lang ->
                val langHelper = LocalizationHelper(context)
                val langCode = langHelper.setLanguage(if (lang == "English") "en" else "ar")
                viewModel.saveLanguage(langCode)
            },
            expandedStates = expandedStates,
            key = "language"
        )

        // Location Selection
//        ExpandableRow(
//            title = stringResource(R.string.location),
//            options = listOf("GPS", "Map"),
//            selectedOption = viewModel.getSavedLocation(),
//            onOptionSelected = { location -> viewModel.saveLocationOption(location) },
//            expandedStates = expandedStates,
//            key = "location"
//        )

        ExpandableRow(
            title = stringResource(R.string.temperature),
            options = listOf("Kelvin", "Celsius", "Fahrenheit"),
            selectedOption = viewModel.getTemperatureUnit(),
            onOptionSelected = { unit -> viewModel.setTemperatureUnit(unit) },
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
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = Color.Blue)
                        }
                    }
                }
            }
        }
    }
}