package com.example.weatherapp.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.Coordinate
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: WeatherRepository): ViewModel() {
    private val _weatherData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val weatherData = _weatherData.asStateFlow()

    private val _forecastData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val forecastData = _forecastData.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

//    init {
//        getWeatherData(Coordinate(30.6118656, 32.2895872), true)
//        getForecastData(Coordinate(30.6118656, 32.2895872), true)
//    }

    fun getWeatherData(coordinate: Coordinate, isOnline: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repository.getCurrentWeather(coordinate, isOnline)
                data.catch {
                    ex -> _weatherData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
                    .collect{
                        _weatherData.value = DataResponse.Success(it)
                    }
            }catch (ex: Exception){
                _weatherData.value = DataResponse.Failure(ex)
                _toastEvent.emit("An error occurred ${ex.message}")
            }
        }
    }
    fun getForecastData(coordinate: Coordinate, isOnline: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = repository.getForecastWeather(coordinate, isOnline)
                data.catch {
                        ex -> _forecastData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
                    .collect{
                        _forecastData.value = DataResponse.Success(it)
                    }
            }catch (ex: Exception){
                _forecastData.value = DataResponse.Failure(ex)
                _toastEvent.emit("An error occurred ${ex.message}")
            }
        }
    }
}

class HomeFactory(private val repo: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}