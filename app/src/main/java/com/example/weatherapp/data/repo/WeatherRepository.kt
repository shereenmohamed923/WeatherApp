package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import com.example.weatherapp.data.local.entities.WeatherAlertsEntity

import com.example.weatherapp.data.model.Coord
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<CurrentWeatherEntity>
    suspend fun addCurrentWeather(currentWeather: CurrentWeatherEntity)
    suspend fun removeCurrentWeather()

    suspend fun getForecastWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<List<ForecastEntity>>
    suspend fun addForecast(forecast: List<ForecastEntity>)

    suspend fun getAllFavoriteCities(): Flow<List<CurrentWeatherEntity>>
    suspend fun getFavoriteCity(coord: Coord, lang:String): Flow<CurrentWeatherEntity>
    suspend fun getFavoriteCityCurrent(cityId: Int, coord: Coord, isOnline: Boolean, lang:String): Flow<CurrentWeatherEntity>
    suspend fun getFavoriteCityForecast(cityId: Int, coord: Coord, isOnline: Boolean, lang:String): Flow<List<ForecastEntity>>
    suspend fun addFavoriteCity(cityCurrentWeather: CurrentWeatherEntity)
    suspend fun removeFavoriteCity(cityId: Int)

    suspend fun addAlert(alert: WeatherAlertsEntity): Long
    suspend fun editAlert(alert: WeatherAlertsEntity)
    suspend fun removeAlert(alert: WeatherAlertsEntity)
    suspend fun getAlertById(alertId: Long): WeatherAlertsEntity
    suspend fun getAllAlerts(): Flow<List<WeatherAlertsEntity>>

}