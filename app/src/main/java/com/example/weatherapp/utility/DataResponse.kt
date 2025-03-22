package com.example.weatherapp.utility

sealed class DataResponse {
    data object Loading: DataResponse()
    data class Success(val data: Any): DataResponse()
    data class Failure(val error: Throwable): DataResponse()
}