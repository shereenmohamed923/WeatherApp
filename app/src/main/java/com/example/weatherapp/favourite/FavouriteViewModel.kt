package com.example.weatherapp.favourite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.FavoriteCityEntity
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.home.getCurrentDateTime
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavouriteViewModel(private val weatherRepository: WeatherRepository): ViewModel() {

    private val _favouritePlaces = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val favouritePlaces = _favouritePlaces.asStateFlow()

    private val _weatherData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val weatherData = _weatherData.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun getAllFavouritePlaces(){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getAllFavoriteCities()
                .catch { ex ->
                    _favouritePlaces.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch your favourite ${ex.message}")
                }
                .collect{ places ->
                   // Log.d("favourite", "Response: ${places[0].lat}, ${places[0].lon}, ${places[0].cityName}")
                    _favouritePlaces.value = DataResponse.Success(places)
                }
        }
    }

    private fun addFavouritePlace(city: FavoriteCityEntity){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.addFavoriteCity(city)
            }catch (e: Exception){
                _toastEvent.emit("Couldn't add your place to favourites ${e.message}")
            }
        }
    }

    fun getWeatherData(coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = weatherRepository.getCurrentWeather(coord, isOnline, lang)
                .map { data ->
                    data.copy(dt = getCurrentDateTime())
                }
                .catch { ex ->
                    _weatherData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
            if (isOnline) {
                val convertedData = data.map { response ->
                    FavoriteCityEntity(
                        cityId = response.id,
                        cityName = response.name,
                        lat = response.coord.lat,
                        lon = response.coord.lon,
                        weatherDescription = response.weather[0].description,
                        weatherIcon = response.weather[0].icon,
                    )
                }

                try {
                    addFavouritePlace(city = convertedData.first())
                } catch (e: Exception) {
                    Log.e("favourite viewmodel", "addCurrentWeather: no data to show")
                }
            }
            data.collect { updatedData ->
                Log.d("date update", "Response: ${updatedData.dt}")
                _weatherData.value = DataResponse.Success(updatedData)
            }
        }
    }
}
class FavouriteFactory(private val weatherRepository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(weatherRepository) as T
    }
}