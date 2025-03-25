package com.example.weatherapp.home

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.example.weatherapp.data.repo.SettingRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.utility.DataResponse
import com.example.weatherapp.utility.UnitHelper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen() {
    val context:Context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(factory = HomeFactory(
        WeatherRepositoryImpl.getInstance(RemoteDataSourceImpl(RetrofitHelper.service)),
        SettingRepositoryImpl.getInstance(context)))

    val currentWeatherState = homeViewModel.weatherData.collectAsState()
    val hourlyForecastState = homeViewModel.hourlyForecastData.collectAsState()
    val dailyForecastState = homeViewModel.dailyForecastData.collectAsState()
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
                CircularProgressIndicator()
            }
            is DataResponse.Success -> {
                if(state.data is CurrentWeatherResponse){
                    val currentWeather = state.data
                    Text(
                        text = currentWeather.name,
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = currentWeather.weather[0].description,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(Modifier.height(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.cloudy_weather),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.width(294.dp).height(141.dp),
                        contentScale = ContentScale.Fit
                    )
                    val (convertedTemp, unitSymbol) = UnitHelper().convertTemperature(currentWeather.main.temp, temperatureUnit, context)
                    Text(text = "$convertedTemp $unitSymbol",
                        fontSize = 64.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = getCurrentDateTime(),
                        fontSize = 16.sp,
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
                                HourlyForecast(forecastWeather.list, temperatureUnit)
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
                                DailyForecast(forecastWeather.list, temperatureUnit)
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
            label = stringResource(R.string.pressure),
            image = R.drawable.pressure
        )
        WeatherDetailItem(
            value = currentWeather.main.humidity.toString()+"%",
            label = stringResource(R.string.humidity),
            image = R.drawable.humidity
        )
        WeatherDetailItem(
            value = currentWeather.wind.speed.toString()+" "+ stringResource(R.string.meter_per_sec),
            label = stringResource(R.string.wind_speed),
            image = R.drawable.wind
        )
        WeatherDetailItem(
            value = currentWeather.clouds.all.toString()+"%",
            label = stringResource(R.string.cloudiness),
            image = R.drawable.cloudiness
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
            modifier = Modifier.size(25.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(Modifier.height(8.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
    }
}

@Composable
fun HourlyForecast(forecast: List<ForecastItem>, temperatureUnit: String) {
    val context:Context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(stringResource(R.string.hourly_forecast_header), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(Modifier.height(8.dp))
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(forecast) { item ->
                val (convertedTemp, unitSymbol) = UnitHelper().convertTemperature(item.main.temp, temperatureUnit, context)
                HourlyForecastItem(item.dt_txt, "$convertedTemp $unitSymbol", R.drawable.cloudy_weather)
            }
        }
    }

}

@Composable
fun HourlyForecastItem(time: String, temperature: String, iconRes: Int) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF957DCD),
                            Color(0xFF523D7F)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = time,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text = "$temperature°",
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun DailyForecast(forecast: List<ForecastItem>, temperatureUnit: String) {
    val context:Context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.Start,
    ) {
        Text(stringResource(R.string.daily_forecast_header), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(Modifier.height(8.dp))
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(forecast) { item ->
                val (convertedTemp, unitSymbol) = UnitHelper().convertTemperature(item.main.temp, temperatureUnit, context)
                DailyForecastItem(item.dt_txt, "$convertedTemp $unitSymbol", item.weather[0].description, R.drawable.cloudy_weather)
            }
        }
    }
}

@Composable
fun DailyForecastItem(
    date: String,
    temperature: String,
    weatherCondition: String,
    iconRes: Int
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(12.dp))
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF957DCD),
                            Color(0xFF523D7F)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Column (
                        verticalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = date,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = weatherCondition,
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = temperature,
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
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

    val outputFormatter = DateTimeFormatter.ofPattern("h a")
    return dateTime.format(outputFormatter)
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDailyTime(dateTimeString: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val date = LocalDate.parse(dateTimeString, formatter)

    return date.dayOfWeek.toString()
}
