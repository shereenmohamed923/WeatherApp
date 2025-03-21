package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.Coordinate
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getCurrentWeather(coordinate: Coordinate): Flow<CurrentWeatherResponse>
    suspend fun getForecastWeather(coordinate: Coordinate): Flow<WeatherForecastResponse>
}