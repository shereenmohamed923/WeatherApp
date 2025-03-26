package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getCurrentWeather(coord: Coord, lang:String): Flow<CurrentWeatherResponse>
    suspend fun getForecastWeather(coord: Coord, lang:String): Flow<ForecastResponse>
}