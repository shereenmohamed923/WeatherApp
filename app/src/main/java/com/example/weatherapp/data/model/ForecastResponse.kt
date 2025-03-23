package com.example.weatherapp.data.model

data class ForecastResponse (
    val city: City,
    val list: List<ForecastItem>
)