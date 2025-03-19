package com.example.weatherapp.data.model

data class CurrentWeatherResponse (
    val coord: Coord,
    val main: Main,
    val clouds: Clouds,
    val weather: List<WeatherItem>,
    val name: String,
    val wind: Wind
)