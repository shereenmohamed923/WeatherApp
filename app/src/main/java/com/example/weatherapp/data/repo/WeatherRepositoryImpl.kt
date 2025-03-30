package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.FavoriteCityEntity
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
): WeatherRepository {

    override suspend fun getCurrentWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<CurrentWeatherResponse> {
        return if(isOnline) {
            remoteDataSource.getCurrentWeather(coord = coord, lang = lang)
        }
        else{
            val currentWeather = localDataSource.getCurrentWeather().map { currentWeatherEntity ->
                CurrentWeatherResponse(
                    id = currentWeatherEntity.cityId,
                    coord = Coord(currentWeatherEntity.lat, currentWeatherEntity.lon),
                    main = Main(currentWeatherEntity.temperature, currentWeatherEntity.humidity, currentWeatherEntity.pressure),
                    clouds = Clouds(currentWeatherEntity.clouds),
                    weather = listOf(Weather(currentWeatherEntity.weatherIcon, currentWeatherEntity.weatherDescription)) ,
                    name = currentWeatherEntity.cityName,
                    wind = Wind(currentWeatherEntity.windSpeed),
                    dt = currentWeatherEntity.lastUpdatedDate
                )
            }
                return currentWeather
        }
    }

    override suspend fun addCurrentWeather(currentWeather: CurrentWeatherEntity) {
        return localDataSource.insertCurrentWeather(currentWeather = currentWeather)
    }

    override suspend fun removeCurrentWeather(currentWeather: CurrentWeatherEntity) {
        return localDataSource.deleteCurrentWeather(currentWeather = currentWeather)
    }

    override suspend fun getForecastWeather(coord: Coord, isOnline: Boolean, lang:String): Flow<ForecastResponse> {
        return if(isOnline) {
            remoteDataSource.getForecastWeather(coord, lang)
        }
        else{
            localDataSource.getForecast().map { forecastList ->
                if (forecastList.isEmpty()) {
                    return@map ForecastResponse(city = City(0, "", Coord(0.0, 0.0)), list = emptyList())
                }
                val firstEntity = forecastList.firstOrNull() ?: return@map ForecastResponse(
                    city = City(0, "Unknown", Coord(0.0, 0.0)),
                    list = emptyList()
                )
                val city = City(
                    id = firstEntity.homeCityId,
                    name = firstEntity.cityName,
                    coord = Coord(firstEntity.lat, firstEntity.lon)
                )
                val forecastItems = forecastList.map { entity ->
                    ForecastItem(
                        dt_txt = entity.dateTime,
                        weather = listOf(Weather(entity.weatherIcon, entity.weatherDescription)),
                        main = Main(entity.temperature, 0, 0)
                    )
                }
                ForecastResponse(
                    city = city,
                    list = forecastItems
                )
            }
        }
        //        return if(isOnline) {
//            return remoteDataSource.getForecastWeather(coord, lang)
//        }
//        else{
//            localDataSource.getWeatherForecast()
//        }
    }

    override suspend fun addForecast(forecast: List<ForecastEntity>) {
        return localDataSource.insertForecast(forecast = forecast)
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