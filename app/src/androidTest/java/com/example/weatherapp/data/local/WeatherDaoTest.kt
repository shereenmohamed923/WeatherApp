package com.example.weatherapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherDao

    @Before
    fun setup(){
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).build()

        dao = database.weatherDao()
    }

    @After
    fun teardown(){
        database.close()
    }

    @Test
    fun insertFavoriteCity_currentWeatherEntity_storeCityInDatabase() = runTest {
        //given
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

        //when
        dao.insertFavoriteCity(cityWeather)

        //then
        val result = dao.getFavoriteCityCurrent(1).first()
        assertNotNull(result)
        assertThat(result, `is`(cityWeather))
    }

    @Test
    fun deleteFavoriteCity_currentWeatherEntity_deleteCityFromDatabase() = runTest {
        //given
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
        dao.insertFavoriteCity(cityWeather)

        //when
        dao.deleteFavoriteCity(1)

        //then
        val result = dao.getFavoriteCityCurrent(1).first()
        assertNull(result)
    }
}