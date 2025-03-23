package com.example.weatherapp.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.R

@Composable
fun SettingScreen(viewModel: SettingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Select Language", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        LanguageSwitcher(viewModel)
    }
}

@Composable
fun LanguageSwitcher(viewModel: SettingViewModel){
    val context = LocalContext.current
    Column {
        Button(onClick = {
            viewModel.setLanguage(context, "en")
        }) {
            Text("Switch to English")
        }
        Button(onClick = {
            viewModel.setLanguage(context, "ar")
        }) {
            Text("Switch to Arabic")
        }
        Text(text = stringResource(R.string.setting), color = Color.Black)
    }
}