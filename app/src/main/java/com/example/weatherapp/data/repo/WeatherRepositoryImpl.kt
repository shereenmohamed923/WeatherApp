package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.DailyForecastEntity
import com.example.weatherapp.data.local.entities.FavoriteCityEntity
import com.example.weatherapp.data.local.entities.HourlyForecastEntity
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
): WeatherRepository {

    override suspend fun getCurrentWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<CurrentWeatherResponse> {
        return remoteDataSource.getCurrentWeather(coord = coord, lang = lang)
    //        return if(isOnline) {
//            remoteDataSource.getCurrentWeather(coord = coord, lang = lang)
//        }
//        else{
//            //localDataSource.getCurrentWeather()
//        }
    }

    override suspend fun addCurrentWeather(currentWeather: CurrentWeatherEntity) {
        return localDataSource.insertCurrentWeather(currentWeather = currentWeather)
    }

    override suspend fun removeCurrentWeather(currentWeather: CurrentWeatherEntity) {
        return localDataSource.deleteCurrentWeather(currentWeather = currentWeather)
    }

    override suspend fun getForecastWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<ForecastResponse> {
        return remoteDataSource.getForecastWeather(coord, lang)
        //        return if(isOnline) {
//            return remoteDataSource.getForecastWeather(coord, lang)
//        }
//        else{
//            localDataSource.getWeatherForecast()
//        }
    }

    override suspend fun addHourlyForecast(forecast: List<HourlyForecastEntity>) {
        return localDataSource.insertHourlyForecast(forecast = forecast)
    }

    override suspend fun addDailyForecast(forecast: List<DailyForecastEntity>) {
        return localDataSource.insertDailyForecast(forecast = forecast)
    }

    override suspend fun getAllFavoriteCities(): Flow<List<FavoriteCityEntity>> {
        return localDataSource.getAllFavoriteCities()
    }

    override suspend fun addFavoriteCity(city: FavoriteCityEntity) {
        localDataSource.insertFavoriteCity(city)
    }

    override suspend fun removeFavoriteCity(city: FavoriteCityEntity) {
        localDataSource.deleteFavoriteCity(city)
    }

    companion object{
        private var INSTANCE: WeatherRepositoryImpl?= null
        fun getInstance(remoteDataSource: RemoteDataSource, localDataSource: LocalDataSource): WeatherRepository {
            return INSTANCE ?: synchronized(this){
                val temp = WeatherRepositoryImpl(remoteDataSource, localDataSource)
                INSTANCE =temp
                temp
            }
        }
    }
}