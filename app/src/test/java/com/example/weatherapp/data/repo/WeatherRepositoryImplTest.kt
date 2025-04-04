package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.remote.RemoteDataSource
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class WeatherRepositoryImplTest {

    private lateinit var repository: WeatherRepositoryImpl

    private val remoteDataSource: RemoteDataSource = mockk()
    private val localDataSource: LocalDataSource = mockk(relaxed = true)

    @Before
    fun setUp() {
        repository = WeatherRepositoryImpl(
            remoteDataSource,
            localDataSource
        )
    }

    @Test
    fun addFavouritePlace_currentWeatherEntity_shouldCallInsertFavoriteCityFromLocalDataSource() = runTest {
        //given a CurrentWeatherEntity to add
        val cityWeather = CurrentWeatherEntity(
            cityId = 1,
            cityName = "",
            lat = 0.0,
            lon = 0.0,
            temperature = 0.0,
            pressure = 0,
            humidity = 0,
            windSpeed = 0.0,
            clouds = 0,
            weatherDescription = "",
            weatherIcon = "",
            lastUpdatedDate = 0.0,
            isFav = true
        )

        //when add this place to favorites
        repository.addFavoriteCity(cityWeather)

        //then addFavoriteCity() in repository is called
        coVerify {
            localDataSource.insertFavoriteCity(cityWeather)
        }
    }

    @Test
    fun removeFavouritePlace_favoritePlaceCityId_shouldCallDeleteFavoriteCityFromLocalDataSource() = runTest {
        //given cityId of Favorite city
        val cityId = 1

        //when remove this place from favorites
        repository.removeFavoriteCity(cityId)

        //then removeFavoriteCity() in repository is called
        coVerify {
            localDataSource.deleteFavoriteCity(cityId)
        }
    }
}