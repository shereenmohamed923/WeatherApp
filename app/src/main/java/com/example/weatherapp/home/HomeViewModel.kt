package com.example.weatherapp.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.ForecastItem
import com.example.weatherapp.data.repo.SettingRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val repository: WeatherRepository,
    private val settingRepository: SettingRepository
    ): ViewModel() {
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
            settingRepository.unitFlow.collectLatest { unit ->
                _temperatureUnit.value = unit
                refreshWeatherData()
            }
        }
    }

    fun getWeatherData(coord: Coord, isOnline: Boolean, lang: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repository.getCurrentWeather(coord, isOnline, lang)
                data.catch {
                    ex -> _weatherData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
                    .collect{
                        Log.d("WeatherData", "Response: ${DataResponse.Success(it)}")
                        _weatherData.value = DataResponse.Success(it)
                    }
            }catch (ex: Exception){
                _weatherData.value = DataResponse.Failure(ex)
                _toastEvent.emit("An error occurred ${ex.message}")
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

            } catch (ex: Exception) {
                _hourlyForecastData.value = DataResponse.Failure(ex)
                _toastEvent.emit("An error occurred: ${ex.message}")
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
                _dailyForecastData.value = DataResponse.Failure(ex)
                _toastEvent.emit("An error occurred: ${ex.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshWeatherData() {
        getWeatherData(Coord(30.6118656, 32.2895872), true, lang = settingRepository.getSavedLanguage())
        getDailyForecastData(Coord(30.6118656, 32.2895872), true, lang = settingRepository.getSavedLanguage())
        getHourlyForecastData(Coord(30.6118656, 32.2895872), true, lang = settingRepository.getSavedLanguage())
    }
}

class HomeFactory(private val homeRepo: WeatherRepository, private val settingRepo: SettingRepository): ViewModelProvider.Factory{
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(homeRepo, settingRepo) as T
    }
}