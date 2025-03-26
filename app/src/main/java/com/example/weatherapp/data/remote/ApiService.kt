package com.example.weatherapp.data.remote

import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService{
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(@Query("lat") lat:Double = 30.6118656,
                                  @Query("lon") lon:Double = 32.2895872,
                                  @Query("appid") appid:String = "451666318959ca261cd48d55ee0dcf30",
                                  @Query("lang") lang: String = "en"
    ): Response<CurrentWeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecastWeather(@Query("lat") lat:Double = 30.6118656,
                                  @Query("lon") lon:Double = 32.2895872,
                                  @Query("appid") appid:String = "451666318959ca261cd48d55ee0dcf30",
                                   @Query("lang") lang: String = "en"
    ): Response<ForecastResponse>
}