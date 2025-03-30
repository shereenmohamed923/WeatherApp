package com.example.weatherapp.data.local

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.FavoriteCityEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val dao: WeatherDao): LocalDataSource {

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity) {
       dao.insertCurrentWeather(currentWeather)
    }

    override suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity> {
       return dao.getCurrentWeather()
    }

    override suspend fun deleteCurrentWeather(currentWeather: CurrentWeatherEntity) {
       dao.deleteCurrentWeather(currentWeather)
    }

    override suspend fun insertForecast(forecast: List<ForecastEntity>) {
        dao.insertForecast(forecast)
    }

    override suspend fun getForecast(): Flow<List<ForecastEntity>>  {
        return dao.getForecast()
    }

    override suspend fun insertFavoriteCity(city: FavoriteCityEntity) {
        dao.insertFavoriteCity(city)
    }

    override suspend fun getAllFavoriteCities(): Flow<List<FavoriteCityEntity>> {
        return dao.getAllFavoriteCities()
    }

    override suspend fun deleteFavoriteCity(city: FavoriteCityEntity) {
        dao.deleteFavoriteCity(city)
    }

}