package com.example.weatherapp.data.model

data class ForecastItem (
    var dt_txt: String,
    val weather: List<Weather>,
    val main: Main,
)