package com.example.weatherapp.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.SettingRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val repository: WeatherRepository,
    private val settingRepository: SettingRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _weatherData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val weatherData = _weatherData.asStateFlow()

    private val _dailyForecastData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val dailyForecastData = _dailyForecastData.asStateFlow()

    private val _hourlyForecastData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val hourlyForecastData = _hourlyForecastData.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(settingRepository.getSavedUnit())
    val temperatureUnit = _temperatureUnit.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        viewModelScope.launch {
            val lat: Double
            val lon: Double
            if (locationRepository.getSavedLocation() == Coord(0.0, 0.0)) {
                val location = locationRepository.locationFlow.filterNotNull()
                    .first() //!!!!should be handled if null
                lat = location.latitude
                lon = location.longitude
                locationRepository.saveLocation(lat = location.latitude, lon = location.longitude)
            } else {
                val location = locationRepository.getSavedLocation()
                lat = location.lat
                lon = location.lon
            }
            refreshWeatherData(lat, lon, true)
            Log.d(
                "LocationUpdate",
                "New location received: $lat, $lon"
            )
        }

        viewModelScope.launch {
            val unit: String = try {
                settingRepository.unitFlow.filterNotNull().first()
            } catch (e: Exception) {
                "Kelvin"
            }
            _temperatureUnit.value = unit
        }
    }

    private fun getWeatherData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getCurrentWeather(coord, isOnline, lang)
                .map { data ->
                    data.copy(dt = getCurrentDateTime())
                }
                .catch { ex ->
                    _weatherData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
            if (isOnline) {
                val convertedData = data.map { response ->
                    CurrentWeatherEntity(
                        cityId = response.id,
                        cityName = response.name,
                        lat = response.coord.lat,
                        lon = response.coord.lon,
                        temperature = response.main.temp,
                        pressure = response.main.pressure,
                        humidity = response.main.humidity,
                        windSpeed = response.wind.speed,
                        clouds = response.clouds.all,
                        weatherDescription = response.weather[0].description,
                        weatherIcon = response.weather[0].icon,
                        lastUpdatedDate = getCurrentDateTime()
                    )
                }
                try {
                    repository.addCurrentWeather(currentWeather = convertedData.first())
                } catch (e: Exception) {
                    Log.e("home viewmodel", "addCurrentWeather: no data to show")
                }
            }
            data.collect { updatedData ->
                Log.d("date update", "Response: ${updatedData.dt}")
                _weatherData.value = DataResponse.Success(updatedData)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getForecastWeather(coord, isOnline, lang)
                    .catch { ex ->
                        _hourlyForecastData.value = DataResponse.Failure(ex)
                        _toastEvent.emit("Couldn't fetch data: ${ex.message}")
                    }
                    .map { forecastResponse ->
                        if (isOnline) {
                            val convertedData = forecastResponse.list.mapIndexed { index, forecastItem ->
                                ForecastEntity(
                                    homeCityId = forecastResponse.city.id,
                                    cityName = forecastResponse.city.name,
                                    lat = forecastResponse.city.coord.lat,
                                    lon = forecastResponse.city.coord.lon,
                                    dateTime = forecastItem.dt_txt,
                                    temperature = forecastItem.main.temp,
                                    weatherDescription = forecastItem.weather.firstOrNull()?.description ?: "Unknown",
                                    weatherIcon = forecastItem.weather.firstOrNull()?.icon ?: "01d",
                                )
                            }
                            repository.addForecast(convertedData)
                        }

                        val updatedList = forecastResponse.list
                            .take(8)
                            .map { item ->
                                val formattedTime = formatHourlyTime(item.dt_txt)
                                val updatedTime = if (settingRepository.getSavedLanguage() == "ar") {
                                    val num = formatNumber(formattedTime.split(" ")[0].toInt())
                                    val period = if (formattedTime.split(" ")[1] == "AM") "ص" else "م"
                                    "$num $period"
                                } else {
                                    formattedTime
                                }
                                item.copy(dt_txt = updatedTime)
                            }

                        forecastResponse.copy(list = updatedList)
                    }
                    .collect { updatedForecast ->
                        _hourlyForecastData.value = DataResponse.Success(updatedForecast)
                    }

            } catch (ex: Exception) {
                _hourlyForecastData.value = DataResponse.Failure(ex)
                _toastEvent.emit("Couldn't fetch data ${ex.message}")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyForecastData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getForecastWeather(coord, isOnline, lang)
                    .catch { ex ->
                        _dailyForecastData.value = DataResponse.Failure(ex)
                        _toastEvent.emit("Couldn't fetch data: ${ex.message}")
                    }
                    .collect { forecastResponse ->
                        val groupedByDate = forecastResponse.list
                            .groupBy { item -> item.dt_txt.substring(0, 10) }

                        val dailyAverages = groupedByDate.map { (date, items) ->
                            val avgTemp = items.map { it.main.temp }.average()
                            ForecastItem(
                                dt_txt = formatDailyTime(date),
                                main = items.first().main.copy(temp = avgTemp),
                                weather = items.first().weather
                            )
                        }
                        val fiveDayForecast = dailyAverages.drop(1).take(5)

                        val updatedForecast = forecastResponse.copy(list = fiveDayForecast)
                        _dailyForecastData.value = DataResponse.Success(updatedForecast)
                    }
            } catch (ex: Exception) {
                _weatherData.value = DataResponse.Failure(ex)
                _toastEvent.emit("Couldn't fetch data ${ex.message}")
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshWeatherData(
        lat: Double,
        lon: Double,
        isOnline: Boolean,
        lang: String = settingRepository.getSavedLanguage()
    ) {
        getWeatherData(coord = Coord(lat, lon), isOnline = isOnline, lang = lang)
        getDailyForecastData(coord = Coord(lat, lon), isOnline = isOnline, lang = lang)
        getHourlyForecastData(coord = Coord(lat, lon), isOnline = isOnline, lang = lang)
    }


}

class HomeFactory(
    private val homeRepo: WeatherRepository,
    private val settingRepo: SettingRepository,
    private val locationRepo: LocationRepository
) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(homeRepo, settingRepo, locationRepo) as T
    }
}