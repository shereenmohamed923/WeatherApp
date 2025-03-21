package com.example.weatherapp.utility

import com.example.weatherapp.data.model.CurrentWeatherResponse

sealed class DataResponse {
    data object Loading: DataResponse()
    data class Success(val data: Any): DataResponse()
    data class Failure(val error: Throwable): DataResponse()
}