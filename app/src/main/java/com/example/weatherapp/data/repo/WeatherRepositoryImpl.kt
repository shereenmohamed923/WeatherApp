package com.example.weatherapp.data.repo

import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse
import com.example.weatherapp.data.remote.RemoteDataSource

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
//    private val localDataSource: LocalDataSource
): WeatherRepository {

    override suspend fun getCurrentWeather(isOnline: Boolean): CurrentWeatherResponse? {
        return remoteDataSource.getCurrentWeather()
    //        return if(isOnline) {
//            remoteDataSource.getCurrentWeather()
//        }
//        else{
//            localDataSource.getAllProducts()
//        }
    }

    override suspend fun getForecastWeather(isOnline: Boolean): WeatherForecastResponse? {
        return remoteDataSource.getForecastWeather()
    }

    companion object{
        private var INSTANCE: WeatherRepositoryImpl?= null
        fun getInstance(remoteDataSource: RemoteDataSource): WeatherRepository {
            return INSTANCE ?: synchronized(this){
                val temp = WeatherRepositoryImpl(remoteDataSource)
                INSTANCE =temp
                temp
            }
        }
    }
}