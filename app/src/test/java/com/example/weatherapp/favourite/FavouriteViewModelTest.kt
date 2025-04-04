package com.example.weatherapp.favourite

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.repo.SettingRepository
import com.example.weatherapp.data.repo.WeatherRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test


class FavouriteViewModelTest {

    private lateinit var viewModel: FavouriteViewModel
    private val weatherRepository: WeatherRepository = mockk(relaxed = true)
    private val settingRepository: SettingRepository = mockk()

    @Before
    fun setUp() {
        viewModel = FavouriteViewModel(weatherRepository, settingRepository)
    }

    @Test
    fun addFavouritePlace_currentWeatherEntity_shouldCallAddFavoriteCityFromRepository() = runTest {
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
            isFav = false
        )

        //when add this place to favorites
        viewModel.addFavouritePlace(cityWeather)

        //then addFavoriteCity() in repository is called
        coVerify {
            weatherRepository.addFavoriteCity(cityWeather)
        }
    }

    @Test
    fun removeFavouritePlace_favoritePlaceCityId_shouldCallRemoveFavoriteCityFromRepository() = runTest {
        //given cityId of Favorite city
        val cityId = 1

        //when remove this place from favorites
        viewModel.removeFavouritePlace(cityId)

        //then removeFavoriteCity() in repository is called
        coVerify {
            weatherRepository.removeFavoriteCity(cityId)
        }
    }
}