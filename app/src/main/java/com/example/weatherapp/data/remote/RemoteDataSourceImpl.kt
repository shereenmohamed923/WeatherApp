package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.Coordinate
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import retrofit2.Response

class RemoteDataSourceImpl(private val service: ApiService): RemoteDataSource {
    override suspend fun getCurrentWeather(coordinate: Coordinate): Flow<CurrentWeatherResponse> = flow {
        val response = service.getCurrentWeather(coordinate.lat, coordinate.lon).body()
        if (response != null) {
            emit(response)
        } else {
            throw Exception("No data received")
        }
    }
    override suspend fun getForecastWeather(coordinate: Coordinate): Flow<WeatherForecastResponse> = flow {
        val response = service.getForecastWeather(coordinate.lat, coordinate.lon).body()
        if (response != null) {
            emit(response)
        } else {
            throw Exception("No data received")
        }
    }
}
