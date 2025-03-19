package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse

interface RemoteDataSource {
    suspend fun getCurrentWeather(): CurrentWeatherResponse?
    suspend fun getForecastWeather(): WeatherForecastResponse?
}