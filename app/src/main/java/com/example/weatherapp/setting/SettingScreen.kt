package com.example.weatherapp.setting

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

        // Language Selection
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

        // Temperature Unit Selection
        ExpandableRow(
            title = stringResource(R.string.temperature),
            options = listOf("Kelvin", "Celsius", "Fahrenheit"),
            selectedOption = viewModel.getTemperatureUnit(),
            onOptionSelected = { unit -> viewModel.saveTemperatureUnit(unit) },
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


//@Composable
//fun SettingScreen(viewModel: SettingViewModel) {
//    val context = LocalContext.current
//    val sharedPreferences = remember { context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE) }
//    val editor = sharedPreferences.edit()
//
//    // State for expanded rows
//    val expandedStates = remember { mutableStateMapOf("language" to false, "location" to false, "temperature" to false, "wind" to false) }
//
//    // Stored preferences
//    var selectedLanguage by remember { mutableStateOf(sharedPreferences.getString("language", "English") ?: "English") }
//    var selectedLocation by remember { mutableStateOf(sharedPreferences.getString("location", "GPS") ?: "GPS") }
//    var selectedTemperature by remember { mutableStateOf(sharedPreferences.getString("temperature", "Celsius") ?: "Celsius") }
//    var selectedWindSpeed by remember { mutableStateOf(sharedPreferences.getString("wind", "meter/sec") ?: "meter/sec") }
//
//    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        item {
//            ExpandableRow(
//                title = "Language",
//                selectedOption = selectedLanguage,
//                options = listOf("English", "Arabic"),
//                expanded = expandedStates["language"] ?: false,
//                onExpandToggle = { expandedStates["language"] = !(expandedStates["language"] ?: false) },
//                onSelectOption = { selected ->
//                    selectedLanguage = selected
//                    editor.putString("language", selected).apply()
//                }
//            )
//        }
//
//        item {
//            ExpandableRow(
//                title = "Location",
//                selectedOption = selectedLocation,
//                options = listOf("GPS", "Select from Map"),
//                expanded = expandedStates["location"] ?: false,
//                onExpandToggle = { expandedStates["location"] = !(expandedStates["location"] ?: false) },
//                onSelectOption = { selected ->
//                    selectedLocation = selected
//                    editor.putString("location", selected).apply()
//                }
//            )
//        }
//
//        item {
//            ExpandableRow(
//                title = "Temperature Unit",
//                selectedOption = selectedTemperature,
//                options = listOf("Kelvin", "Celsius", "Fahrenheit"),
//                expanded = expandedStates["temperature"] ?: false,
//                onExpandToggle = { expandedStates["temperature"] = !(expandedStates["temperature"] ?: false) },
//                onSelectOption = { selected ->
//                    selectedTemperature = selected
//                    editor.putString("temperature", selected).apply()
//                }
//            )
//        }
//
//        item {
//            ExpandableRow(
//                title = "Wind Speed",
//                selectedOption = selectedWindSpeed,
//                options = listOf("meter/sec", "miles/hour"),
//                expanded = expandedStates["wind"] ?: false,
//                onExpandToggle = { expandedStates["wind"] = !(expandedStates["wind"] ?: false) },
//                onSelectOption = { selected ->
//                    selectedWindSpeed = selected
//                    editor.putString("wind", selected).apply()
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun ExpandableRow(
//    title: String,
//    selectedOption: String,
//    options: List<String>,
//    expanded: Boolean,
//    onExpandToggle: () -> Unit,
//    onSelectOption: (String) -> Unit
//) {
//    Column(modifier = Modifier.fillMaxWidth()) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { onExpandToggle() }
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
//            Icon(
//                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
//                contentDescription = null
//            )
//        }
//
//        AnimatedVisibility(visible = expanded) {
//            Column(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, bottom = 8.dp)) {
//                options.forEach { option ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable { onSelectOption(option) }
//                            .padding(8.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(option, fontSize = 16.sp)
//                        Spacer(modifier = Modifier.weight(1f))
//                        if (option == selectedOption) {
//                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Blue)
//                        }
//                    }
//                }
//            }
//        }
//        Divider()
//    }
//}


//@Composable
//fun SettingScreen(viewModel: SettingViewModel) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(text = "Select Language", fontSize = 20.sp, fontWeight = FontWeight.Bold)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        LanguageSwitcher(viewModel)
//        UnitSwitcher(viewModel)
//    }
//}
//
//@Composable
//fun LanguageSwitcher(viewModel: SettingViewModel){
//    val context = LocalContext.current
//    val langHelper = LocalizationHelper(context)
//    Column {
//        Button(onClick = {
//            //viewModel.setLanguage(context, "en")
//            val langCode = langHelper.setLanguage("en")
//            viewModel.saveLanguage(langCode)
//
//        }) {
//            Text("Switch to English")
//        }
//        Button(onClick = {
//           // viewModel.setLanguage(context, "ar")
//            val langCode = langHelper.setLanguage("ar")
//            viewModel.saveLanguage(langCode)
//        }) {
//            Text("Switch to Arabic")
//        }
//        Text(text = stringResource(R.string.setting), color = Color.Black)
//    }
//}
//
//@Composable
//fun UnitSwitcher(viewModel: SettingViewModel){
//    Column {
//        Button(onClick = {
//            viewModel.saveTemperatureUnit("Kelvin")
//        }) {
//            Text("Switch to Kelvin")
//        }
//        Button(onClick = {
//            viewModel.saveTemperatureUnit("Celsius")
//        }) {
//            Text("Switch to Celsius")
//        }
//        Button(onClick = {
//            viewModel.saveTemperatureUnit("Fahrenheit")
//        }) {
//            Text("Switch to Fahrenheit")
//        }
//        Text(text = stringResource(R.string.setting), color = Color.Black)
//    }
//}