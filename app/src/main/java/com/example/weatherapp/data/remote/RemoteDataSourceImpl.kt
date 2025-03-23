package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteDataSourceImpl(private val service: ApiService): RemoteDataSource {
    override suspend fun getCurrentWeather(coord: Coord): Flow<CurrentWeatherResponse> = flow {
        val response = service.getCurrentWeather(coord.lat, coord.lon).body()
        if (response != null) {
            emit(response)
        } else {
            throw Exception("No data received")
        }
    }
    override suspend fun getForecastWeather(coord: Coord): Flow<ForecastResponse> = flow {
        val response = service.getForecastWeather(coord.lat, coord.lon).body()
        if (response != null) {
            emit(response)
        } else {
            throw Exception("No data received")
        }
    }
}
