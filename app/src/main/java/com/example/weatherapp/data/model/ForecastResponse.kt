package com.example.weatherapp.data.model

import com.example.weatherapp.data.local.entities.ForecastEntity

data class ForecastResponse (
    val city: City,
    val list: List<ForecastItem>
)
//fun ForecastResponse.toForecastWeather(): ForecastEntity {
//    return ForecastEntity(
//        homeCityId = city.id,
//        cityName = city.name,
//        lat = city.coord.lat,
//        lon = city.coord.lon,
//        dateTime = list,
//        temperature = TODO(),
//        weatherDescription = TODO(),
//        weatherIcon = TODO()
//    )
//}