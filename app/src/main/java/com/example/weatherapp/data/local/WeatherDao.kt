package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.DailyForecastEntity
import com.example.weatherapp.data.local.entities.FavoriteCityEntity
import com.example.weatherapp.data.local.entities.HourlyForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity)

    @Query("SELECT * FROM current_weather")
    fun getCurrentWeather(): Flow<CurrentWeatherEntity>

    @Delete
    suspend fun deleteCurrentWeather(currentWeather: CurrentWeatherEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHourlyForecast(forecast: List<HourlyForecastEntity>)

    @Query("SELECT * FROM hourly_forecast ORDER BY dateTime ASC")
    fun getHourlyForecast(): Flow<List<HourlyForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyForecast(forecast: List<DailyForecastEntity>)

    @Query("SELECT * FROM daily_forecast ORDER BY date ASC")
    fun getDailyForecast(): Flow<List<DailyForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCity(city: FavoriteCityEntity)

    @Query("SELECT * FROM favorite_cities")
    fun getAllFavoriteCities(): Flow<List<FavoriteCityEntity>>

    @Delete
    suspend fun deleteFavoriteCity(city: FavoriteCityEntity)


}