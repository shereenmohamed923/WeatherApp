package com.example.weatherapp.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.model.ForecastResponse
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.utility.DataResponse
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val factory = HomeFactory(
        WeatherRepositoryImpl.getInstance(RemoteDataSourceImpl(RetrofitHelper.service)))
    val homeViewModel: HomeViewModel = viewModel(factory = factory)
    homeViewModel.getWeatherData(Coord(30.6118656, 32.2895872), true)
    homeViewModel.getDailyForecastData(Coord(30.6118656, 32.2895872), true)
    homeViewModel.getHourlyForecastData(Coord(30.6118656, 32.2895872), true)

    val currentWeatherState = homeViewModel.weatherData.collectAsState()
    val hourlyForecastState = homeViewModel.hourlyForecastData.collectAsState()
    val dailyForecastState = homeViewModel.dailyForecastData.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = currentWeatherState.value) {
            is DataResponse.Loading -> {
                CircularProgressIndicator()
            }
            is DataResponse.Success -> {
                if(state.data is CurrentWeatherResponse){
                    val currentWeather = state.data
                    Text(
                        text = currentWeather.name,
                        fontSize = 32.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentWeather.weather[0].description,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.cloudy_weather),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(250.dp),
                        contentScale = ContentScale.Fit
                    )
                    Text(text = currentWeather.main.temp.toInt().toString(),
                        fontSize = 48.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = getCurrentDateTime(),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(Modifier.height(16.dp))
                    WeatherDetails(currentWeather)
                    Spacer(Modifier.height(16.dp))
                    when (val hourlyForecastWeatherState = hourlyForecastState.value) {
                        is DataResponse.Loading -> {    }
                        is  DataResponse.Success -> {
                            if(hourlyForecastWeatherState.data is ForecastResponse){
                                val forecastWeather = hourlyForecastWeatherState.data
                                HourlyForecast(forecastWeather.list)
                            }
                        }
                        is DataResponse.Failure -> {
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
                    when(val dailyForecastWeatherState = dailyForecastState.value){
                        is DataResponse.Loading -> {    }
                        is  DataResponse.Success -> {
                            if(dailyForecastWeatherState.data is ForecastResponse){
                                val forecastWeather = dailyForecastWeatherState.data
                                DailyForecast(forecastWeather.list)
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

@Composable
fun WeatherDetails(currentWeather: CurrentWeatherResponse) {
    Row(
        modifier = Modifier
            .width(350.dp)
            .background(Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        WeatherDetailItem(
            value = convertPressureToPercentage(currentWeather.main.pressure),
            label = "Pressure",
            image = R.drawable.pressure
        )
        WeatherDetailItem(
            value = currentWeather.main.humidity.toString()+"%",
            label = "Humidity",
            image = R.drawable.humidity
        )
        WeatherDetailItem(
            value = currentWeather.wind.speed.toString()+"m/s",
            "Wind Speed",
            image = R.drawable.wind_speed
        )
        WeatherDetailItem(
            value = currentWeather.clouds.all.toString()+"%",
            label = "Cloudiness",
            image = R.drawable.cloud
        )
    }
}

@Composable
fun WeatherDetailItem(value: String, label: String, image:Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = "Icon",
            modifier =
            if(image == R.drawable.cloud)   Modifier.size(40.dp)
            else    Modifier.size(25.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(Modifier.height(8.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun HourlyForecast(forecast: List<ForecastItem>) {
    Text("Today", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(forecast) { item ->
            HourlyForecastItem(item.dt_txt, item.main.temp.toInt().toString())
        }
    }
}

@Composable
fun HourlyForecastItem(time: String, temp: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
        Text(time, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
        Spacer(Modifier.height(4.dp))
        Text(temp, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun DailyForecast(forecast: List<ForecastItem>) {
    Text("7-Day Forecast", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(forecast) { item ->
            DailyForecastItem(item.dt_txt, item.main.temp.toInt().toString())
        }
    }
}

@Composable
fun DailyForecastItem(day: String, temp: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
        Text(day, fontSize = 16.sp, color = Color.White)
        Text(temp, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}



//----------------------------helper functions should be removed to utilities--------------------------------

fun getCurrentDateTime(): String {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("EEEE, dd MMMM yyyy | HH:mm", Locale.getDefault())
    return formatter.format(calendar.time)
}
fun convertPressureToPercentage(pressure: Int, minPressure: Int = 980, maxPressure: Int = 1050): String {
    return ((pressure - minPressure).toFloat() / (maxPressure - minPressure) * 100).toInt().toString()+"%"
}
@RequiresApi(Build.VERSION_CODES.O)
fun formatHourlyTime(dateTimeString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(dateTimeString, formatter)

    val outputFormatter = DateTimeFormatter.ofPattern("h a") // "3 AM" or "3 PM"
    return dateTime.format(outputFormatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDailyTime(dateTimeString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateTimeString, formatter)

    return date.dayOfWeek.toString() // "Tuesday"
}
