package com.example.weatherapp.data.repo

import com.example.weatherapp.data.model.Coordinate
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface WeatherRepository {
    suspend fun getCurrentWeather(coordinate: Coordinate, isOnline: Boolean): Flow<CurrentWeatherResponse>
    suspend fun getForecastWeather(coordinate: Coordinate, isOnline: Boolean): Flow<WeatherForecastResponse>

}