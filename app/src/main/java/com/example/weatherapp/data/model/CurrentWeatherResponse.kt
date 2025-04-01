package com.example.weatherapp.data.model

import com.example.weatherapp.data.local.entities.CurrentWeatherEntity

data class CurrentWeatherResponse (
    val id: Int, //city ID
    val coord: Coord,
    val main: Main,
    val clouds: Clouds,
    val weather: List<Weather>,
    val name: String,
    val wind: Wind,
    var dt:Double
    )
fun CurrentWeatherResponse.toHomeWeather(): CurrentWeatherEntity{
    return CurrentWeatherEntity(
        cityId = id,
        cityName = name,
        lat = coord.lat,
        lon = coord.lon,
        temperature = main.temp,
        pressure = main.pressure,
        humidity = main.humidity,
        windSpeed = wind.speed,
        clouds = clouds.all,
        weatherDescription = weather[0].description,
        weatherIcon = weather[0].icon,
        lastUpdatedDate = dt
    )
}