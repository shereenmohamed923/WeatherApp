package com.example.weatherapp.data.repo

import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse

interface WeatherRepository {
    suspend fun getCurrentWeather(isOnline: Boolean): CurrentWeatherResponse?
    suspend fun getForecastWeather(isOnline: Boolean): WeatherForecastResponse?

}