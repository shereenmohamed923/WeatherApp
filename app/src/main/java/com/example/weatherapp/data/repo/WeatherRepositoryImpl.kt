package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import com.example.weatherapp.data.model.City
import com.example.weatherapp.data.model.Clouds
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.model.Main
import com.example.weatherapp.data.model.Weather
import com.example.weatherapp.data.model.Wind
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
): WeatherRepository {

    override suspend fun getCurrentWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<CurrentWeatherEntity> {
        return if(isOnline) {
            val currentWeather = remoteDataSource.getCurrentWeather(coord = coord, lang = lang).map { response ->
                CurrentWeatherEntity(
                    cityId = response.id,
                    cityName = response.name,
                    lat = response.coord.lat,
                    lon = response.coord.lon,
                    temperature = response.main.temp,
                    pressure = response.main.pressure,
                    humidity = response.main.humidity,
                    windSpeed = response.wind.speed,
                    clouds = response.clouds.all,
                    weatherDescription = response.weather[0].description,
                    weatherIcon = response.weather[0].icon,
                    lastUpdatedDate = response.dt,
                )
            }
            return currentWeather
        }
        else{
            localDataSource.getCurrentWeather()

        }
    }

    override suspend fun addCurrentWeather(currentWeather: CurrentWeatherEntity) {
        return localDataSource.insertCurrentWeather(currentWeather = currentWeather)
    }

    override suspend fun removeCurrentWeather() {
        return localDataSource.deleteCurrentWeather()
    }

    override suspend fun getForecastWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<List<ForecastEntity>> {
        return if(isOnline) {
            val convertedData = remoteDataSource.getForecastWeather(coord, lang)
            .map{   forecastResponse ->
                forecastResponse.list.map {forecastItem ->
                    ForecastEntity(
                        homeCityId = forecastResponse.city.id,
                        cityName = forecastResponse.city.name,
                        lat = forecastResponse.city.coord.lat,
                        lon = forecastResponse.city.coord.lon,
                        dateTime = forecastItem.dt_txt,
                        temperature = forecastItem.main.temp,
                        weatherDescription = forecastItem.weather[0].description,
                        weatherIcon = forecastItem.weather[0].icon,
                    )
                }
            }
            return convertedData
        }
        else{
            localDataSource.getForecast()
        }
    }

    override suspend fun addForecast(forecast: List<ForecastEntity>) {
        return localDataSource.insertForecast(forecast = forecast)
    }


    override suspend fun getAllFavoriteCities(): Flow<List<CurrentWeatherEntity>> {
        return localDataSource.getAllFavoriteCities()
    }

    override suspend fun getFavoriteCity(coord: Coord, lang: String): Flow<CurrentWeatherEntity> {
        val currentWeather = remoteDataSource.getCurrentWeather(coord = coord, lang = lang).map { response ->
            CurrentWeatherEntity(
                cityId = response.id,
                cityName = response.name,
                lat = response.coord.lat,
                lon = response.coord.lon,
                temperature = response.main.temp,
                pressure = response.main.pressure,
                humidity = response.main.humidity,
                windSpeed = response.wind.speed,
                clouds = response.clouds.all,
                weatherDescription = response.weather[0].description,
                weatherIcon = response.weather[0].icon,
                lastUpdatedDate = response.dt,
            )
        }
        return currentWeather
    }

    override suspend fun getFavoriteCityCurrent(cityId: Int, coord: Coord, isOnline: Boolean, lang:String): Flow<CurrentWeatherEntity> {
        return if(isOnline) {
            val currentWeather = remoteDataSource.getCurrentWeather(coord = coord, lang = lang).map { response ->
                CurrentWeatherEntity(
                    cityId = response.id,
                    cityName = response.name,
                    lat = response.coord.lat,
                    lon = response.coord.lon,
                    temperature = response.main.temp,
                    pressure = response.main.pressure,
                    humidity = response.main.humidity,
                    windSpeed = response.wind.speed,
                    clouds = response.clouds.all,
                    weatherDescription = response.weather[0].description,
                    weatherIcon = response.weather[0].icon,
                    lastUpdatedDate = response.dt,
                )
            }
            return currentWeather
        }
        else{
            localDataSource.getFavoriteCityCurrent(cityId)
        }
    }


    override suspend fun getFavoriteCityForecast(cityId: Int, coord: Coord, isOnline: Boolean, lang:String): Flow<List<ForecastEntity>> {
        return if(isOnline) {
            val convertedData = remoteDataSource.getForecastWeather(coord, lang)
                .map{   forecastResponse ->
                    forecastResponse.list.map {forecastItem ->
                        ForecastEntity(
                            homeCityId = forecastResponse.city.id,
                            cityName = forecastResponse.city.name,
                            lat = forecastResponse.city.coord.lat,
                            lon = forecastResponse.city.coord.lon,
                            dateTime = forecastItem.dt_txt,
                            temperature = forecastItem.main.temp,
                            weatherDescription = forecastItem.weather[0].description,
                            weatherIcon = forecastItem.weather[0].icon,
                        )
                    }
                }
            return convertedData
        }
        else{
            localDataSource.getFavoriteCityForecast(cityId)
        }
    }

    override suspend fun addFavoriteCity(cityCurrentWeather: CurrentWeatherEntity) {
        localDataSource.insertFavoriteCity(cityCurrentWeather)
    }

    override suspend fun removeFavoriteCity(cityId: Int){
        localDataSource.deleteFavoriteCity(cityId)
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