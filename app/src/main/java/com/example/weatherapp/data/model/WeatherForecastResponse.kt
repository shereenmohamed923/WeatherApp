package com.example.weatherapp.data.model

data class WeatherForecastResponse (
    val city: City,
    val list: List<ListItem>
)