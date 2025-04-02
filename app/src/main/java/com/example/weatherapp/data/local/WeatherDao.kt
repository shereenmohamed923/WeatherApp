package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeatherEntity) // isFav = false

    @Query("SELECT * FROM current_weather WHERE isFav = 0") //where isFav = false
    fun getCurrentWeather(): Flow<CurrentWeatherEntity>

    @Query("DELETE FROM current_weather WHERE isFav = 0")
    suspend fun deleteCurrentWeather() //where isFav = false

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: List<ForecastEntity>) //isFav = false

    @Query("SELECT * FROM forecast_table WHERE isFav = 0") //where isFav = false
    fun getForecast(): Flow<List<ForecastEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteCity(currentWeather: CurrentWeatherEntity) //isFav = true

    @Query("SELECT * FROM current_weather WHERE isFav = 1")
    fun getAllFavoriteCities(): Flow<List<CurrentWeatherEntity>> //where isFav = true

    //add function to get fav city details (cityId) where isFav = true && id = cityId
    @Query("SELECT * FROM current_weather WHERE isFav = 1 AND cityId = :cityId")
    fun getFavoriteCityCurrent(cityId: Int): Flow<CurrentWeatherEntity> //where isFav = true

    @Query("SELECT * FROM forecast_table WHERE isFav = 1 AND homeCityId = :cityId")
    fun getFavoriteCityForecast(cityId: Int): Flow<List<ForecastEntity>> //where isFav = true

    @Query("DELETE FROM current_weather WHERE isFav = 1 AND cityId = :cityId")
    suspend fun deleteFavoriteCity(cityId: Int) //where isFav = true && id = cityId

}