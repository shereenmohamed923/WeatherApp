package com.example.weatherapp.favourite

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.LocationRepositoryImpl
import com.example.weatherapp.data.repo.SettingRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.home.DailyForecast
import com.example.weatherapp.home.HomeFactory
import com.example.weatherapp.home.HomeViewModel
import com.example.weatherapp.home.HourlyForecast
import com.example.weatherapp.home.WeatherDetails
import com.example.weatherapp.home.formatCurrentDateTime
import com.example.weatherapp.home.formatNumber
import com.example.weatherapp.utility.DataResponse
import com.example.weatherapp.utility.UnitHelper

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoriteDetailsScreen(favouriteViewModel: FavouriteViewModel) {
    val context: Context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeFactory(
            WeatherRepositoryImpl.getInstance(
                RemoteDataSourceImpl(RetrofitHelper.service),
                LocalDataSourceImpl(WeatherDatabase.getInstance(context).weatherDao())
            ),
            SettingRepositoryImpl.getInstance(context),
            LocationRepositoryImpl.getInstance(context)
        )
    )

    val currentWeatherState = favouriteViewModel.weatherData.collectAsState()
    val hourlyForecastState = favouriteViewModel.hourlyForecastData.collectAsState()
    val dailyForecastState = favouriteViewModel.dailyForecastData.collectAsState()
    val temperatureUnit by homeViewModel.temperatureUnit.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = currentWeatherState.value) {
            is DataResponse.Loading -> {
                Log.i("HomeScreen", "currentWeatherState: loading")
                CircularProgressIndicator()
            }

            is DataResponse.Success -> {

                if (state.data is CurrentWeatherEntity) {
                    val currentWeather = state.data
                    Text(
                        text = currentWeather.cityName,
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentWeather.weatherDescription,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.cloudy_weather),
                        contentDescription = "Weather Icon",
                        modifier = Modifier
                            .width(294.dp)
                            .height(141.dp),
                        contentScale = ContentScale.Fit
                    )
                    val (convertedTemp, unitSymbol) = UnitHelper().convertTemperature(
                        currentWeather.temperature,
                        temperatureUnit,
                        context
                    )
                    Text(
                        text = "${formatNumber(convertedTemp)} $unitSymbol",
                        fontSize = 64.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatCurrentDateTime(currentWeather.lastUpdatedDate),
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(16.dp))
                    WeatherDetails(currentWeather)
                    Spacer(Modifier.height(16.dp))
                    Log.i("HomeScreen", "before: loading")
                    when (val hourlyForecastWeatherState = hourlyForecastState.value) {
                        is DataResponse.Loading -> {}
                        is DataResponse.Success -> {
                            if (hourlyForecastWeatherState.data is List<*>) {
                                Log.i("HomeScreen", "ForecastResponse: ")
                                val forecastWeather =
                                    hourlyForecastWeatherState.data as List<ForecastEntity>
                                HourlyForecast(forecastWeather, temperatureUnit)
                            }
                        }

                        is DataResponse.Failure -> {
                            Log.i("HomeScreen", "forecast: failure")
                            val failed = hourlyForecastWeatherState.error
                            LaunchedEffect(failed) {
                                snackBarHostState.showSnackbar(
                                    message = failed.toString(),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    when (val dailyForecastWeatherState = dailyForecastState.value) {
                        is DataResponse.Loading -> {}
                        is DataResponse.Success -> {
                            if (dailyForecastWeatherState.data is List<*>) {
                                val forecastWeather =
                                    dailyForecastWeatherState.data as List<ForecastEntity>
                                DailyForecast(forecastWeather, temperatureUnit)
                            }
                        }

                        is DataResponse.Failure -> {
                            val failed = dailyForecastWeatherState.error
                            LaunchedEffect(failed) {
                                snackBarHostState.showSnackbar(
                                    message = failed.toString(),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    }
                }
            }

            is DataResponse.Failure -> {
                Log.i("HomeScreen", "currentWeatherState: failure")
                val failed = state.error
                LaunchedEffect(failed) {
                    snackBarHostState.showSnackbar(
                        message = failed.toString(),
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}
