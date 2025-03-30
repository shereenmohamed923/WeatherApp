package com.example.weatherapp.data.model

data class CurrentWeatherResponse (
    val id: Int, //city ID
    val coord: Coord,
    val main: Main,
    val clouds: Clouds,
    val weather: List<Weather>,
    val name: String,
    val wind: Wind,
    var dt:Double
    )