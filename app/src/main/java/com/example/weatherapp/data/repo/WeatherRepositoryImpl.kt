package com.example.weatherapp.data.repo

import com.example.weatherapp.data.model.Coordinate
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.WeatherForecastResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
//    private val localDataSource: LocalDataSource
): WeatherRepository {

    override suspend fun getCurrentWeather(coordinate: Coordinate,isOnline: Boolean): Flow<CurrentWeatherResponse> {
        return remoteDataSource.getCurrentWeather(coordinate)
    //        return if(isOnline) {
//            remoteDataSource.getCurrentWeather()
//        }
//        else{
//            localDataSource.getAllProducts()
//        }
    }

    override suspend fun getForecastWeather(coordinate: Coordinate, isOnline: Boolean): Flow<WeatherForecastResponse> {
        return remoteDataSource.getForecastWeather(coordinate)
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