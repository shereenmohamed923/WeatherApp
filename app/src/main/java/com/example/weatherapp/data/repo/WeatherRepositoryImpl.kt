package com.example.weatherapp.data.repo

import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
//    private val localDataSource: LocalDataSource
): WeatherRepository {

    override suspend fun getCurrentWeather(coord: Coord, isOnline: Boolean): Flow<CurrentWeatherResponse> {
        return remoteDataSource.getCurrentWeather(coord)
    //        return if(isOnline) {
//            remoteDataSource.getCurrentWeather()
//        }
//        else{
//            localDataSource.getAllProducts()
//        }
    }

    override suspend fun getForecastWeather(coord: Coord, isOnline: Boolean): Flow<ForecastResponse> {
        return remoteDataSource.getForecastWeather(coord)
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