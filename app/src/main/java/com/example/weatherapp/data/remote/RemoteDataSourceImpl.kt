package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse

class RemoteDataSourceImpl(private val service: ApiService): RemoteDataSource {
    override suspend fun getCurrentWeather(): CurrentWeatherResponse? {
        return service.getCurrentWeather().body()
    }

    override suspend fun getForecastWeather(): WeatherForecastResponse? {
        return service.getForecastWeather().body()
    }
}
