package com.example.weatherapp.data.model

data class ForecastItem (
    //val dt: Long,
    var dt_txt: String,
    val weather: List<Weather>,
    val main: Main,
    //val clouds: Clouds,
    //val wind: Wind
)