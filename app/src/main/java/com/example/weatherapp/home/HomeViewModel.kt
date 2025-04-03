package com.example.weatherapp.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.Coord
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

    private val _temperatureUnit = MutableStateFlow(settingRepository.getSavedTemperatureUnit())
    val temperatureUnit = _temperatureUnit.asStateFlow()

    private val _windSpeedUnit = MutableStateFlow(settingRepository.getSavedWindSpeedUnit())
    val windSpeedUnit = _windSpeedUnit.asStateFlow()

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
            refreshWeatherData(lat, lon)
            Log.d(
                "LocationUpdate",
                "New location received: $lat, $lon"
            )
        }

        viewModelScope.launch {
            val unit: String = try {
                settingRepository.temperatureUnitFlow.filterNotNull().first()
            } catch (e: Exception) {
                "Kelvin"
            }
            _temperatureUnit.value = unit
        }
        viewModelScope.launch {
            val unit: String = try {
                settingRepository.windSpeedUnitFlow.filterNotNull().first()
            } catch (e: Exception) {
                "meter/second"
            }
            _windSpeedUnit.value = unit
        }
    }

    private fun getWeatherData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repository.getCurrentWeather(coord, isOnline, lang)
                .map { data ->
                    data.copy(lastUpdatedDate = getCurrentDateTime())
                }
                .catch { ex ->
                    _weatherData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
            data.collect { updatedData ->
                Log.d("date update", "Response: $updatedData")
                locationRepository.saveLocationName(updatedData.cityName)
                _weatherData.value = DataResponse.Success(updatedData)
                if (isOnline) {
                    try {
                        repository.removeCurrentWeather()
                        repository.addCurrentWeather(currentWeather = updatedData)
                    } catch (e: Exception) {
                        Log.e("home viewmodel", "addCurrentWeather: no data to show")
                    }
                }
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
                    }.map { forecastResponse ->
                        if (isOnline) {
                            try {
                                repository.addForecast(forecast = forecastResponse)
                            } catch (e: Exception) {
                                Log.e("home viewmodel", "addCurrentWeather: no data to show")
                            }
                        }
                        forecastResponse
                            .take(8)
                            .map { item ->
                                val formattedTime = formatHourlyTime(item.dateTime)
                                val updatedTime =
                                    if (settingRepository.getSavedLanguage() == "ar") {
                                        val num = formatNumber(formattedTime.split(" ")[0].toInt())
                                        val period =
                                            if (formattedTime.split(" ")[1] == "AM") "ص" else "م"
                                        "$num $period"
                                    } else {
                                        formattedTime
                                    }
                                item.copy(dateTime = updatedTime)
                            }
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
                    .map { forecastList ->
                        val groupedByDate = forecastList.groupBy { it.dateTime.substring(0, 10) }
                        val dailyAverages = groupedByDate.map { (date, items) ->
                            val avgTemp = items.map { it.temperature }.average()

                            val firstItem = items.first().apply {
                                dateTime = formatDailyTime(date)
                                temperature = avgTemp
                            }
                            firstItem
                        }
                        dailyAverages.drop(1).take(5)
                    }
                    .collect { transformedList ->
                        _dailyForecastData.value = DataResponse.Success(transformedList)
                    }
            } catch (ex: Exception) {
                _dailyForecastData.value = DataResponse.Failure(ex)
                _toastEvent.emit("Couldn't fetch data ${ex.message}")
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshWeatherData(
        lat: Double,
        lon: Double,
        isOnline: Boolean = settingRepository.checkNetworkConnection(),
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