package com.example.weatherapp.data.local

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(private val dao: WeatherDao): LocalDataSource {

    override suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity) {
       dao.insertCurrentWeather(currentWeather)
    }

    override suspend fun getCurrentWeather(): Flow<CurrentWeatherEntity> {
       return dao.getCurrentWeather()
    }

    override suspend fun deleteCurrentWeather() {
       dao.deleteCurrentWeather()
    }

    override suspend fun insertForecast(forecast: List<ForecastEntity>) {
        dao.insertForecast(forecast)
    }

    override suspend fun getForecast(): Flow<List<ForecastEntity>>  {
        return dao.getForecast()
    }

    override suspend fun insertFavoriteCity(cityCurrentWeather: CurrentWeatherEntity) {
        dao.insertFavoriteCity(cityCurrentWeather)
    }

    override suspend fun getAllFavoriteCities(): Flow<List<CurrentWeatherEntity>> {
        return dao.getAllFavoriteCities()
    }

    override suspend fun getFavoriteCityCurrent(cityId: Int): Flow<List<CurrentWeatherEntity>> {
        return dao.getFavoriteCityCurrent(cityId)
    }

    override suspend fun getFavoriteCityForecast(cityId: Int): Flow<List<ForecastEntity>> {
        return dao.getFavoriteCityForecast(cityId)
    }

    override suspend fun deleteFavoriteCity(cityId: Int) {
        dao.deleteFavoriteCity(cityId)
    }

}