package com.example.weatherapp.home

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.firstOrNull
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
            val location = locationRepository.locationFlow.filterNotNull().first()
            refreshWeatherData(location.latitude, location.longitude, true)
            Log.d(
                "LocationUpdate",
                "New location received: ${location.latitude}, ${location.longitude}"
            )
            locationRepository.saveLocation(lat = location.latitude, lon = location.longitude)
        }

        viewModelScope.launch {
            val unit = settingRepository.unitFlow.filterNotNull().first()
            _temperatureUnit.value = unit
        }
    }

//    fun getSavedLocation(): Coord {
//        return locationRepository.getSavedLocation()
//    }
//
//    fun saveLocation(lat: Double, lon: Double) {
//        locationRepository.saveLocation(lat, lon)
//    }

    private fun getWeatherData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCurrentWeather(coord, isOnline, lang)
                .catch { ex ->
                    _weatherData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
                .collect { data ->
                    Log.d("WeatherData", "Response: $data")
                    _weatherData.value = DataResponse.Success(data)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHourlyForecastData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getForecastWeather(coord, isOnline, lang)
                .catch { ex ->
                    _hourlyForecastData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data: ${ex.message}")
                }
                .collect { forecastResponse ->
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
                    val updatedForecast = forecastResponse.copy(list = updatedList)
                    _hourlyForecastData.value = DataResponse.Success(updatedForecast)
                }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDailyForecastData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
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