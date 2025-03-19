package com.example.weatherapp.data.model

data class ListItem (
    val dt_txt: String,
    val weather: List<WeatherItem>,
    val main: Main,
    val clouds: Clouds,
    val wind: Wind
)