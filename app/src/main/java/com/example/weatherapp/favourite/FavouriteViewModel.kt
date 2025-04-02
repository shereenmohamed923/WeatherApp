package com.example.weatherapp.favourite

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.local.entities.ForecastEntity
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.repo.SettingRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.home.formatDailyTime
import com.example.weatherapp.home.formatHourlyTime
import com.example.weatherapp.home.formatNumber
import com.example.weatherapp.home.getCurrentDateTime
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val weatherRepository: WeatherRepository,
    private val settingRepository: SettingRepository,
    ): ViewModel() {

    private val _favouritePlaces = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val favouritePlaces = _favouritePlaces.asStateFlow()

    private val _favouriteCity = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val favouriteCity = _favouriteCity.asStateFlow()

    private val _weatherData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val weatherData = _weatherData.asStateFlow()

    private val _dailyForecastData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val dailyForecastData = _dailyForecastData.asStateFlow()

    private val _hourlyForecastData = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val hourlyForecastData = _hourlyForecastData.asStateFlow()

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
                    _favouritePlaces.value = DataResponse.Success(places)
                }
        }
    }

    private fun addFavouritePlace(cityCurrentWeather: CurrentWeatherEntity){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.addFavoriteCity(cityCurrentWeather)
            }catch (e: Exception){
                _toastEvent.emit("Couldn't add your place to favourites ${e.message}")
            }
        }
    }
    private fun addFavouriteForecast(cityForecastWeather: List<ForecastEntity>){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.addForecast(cityForecastWeather)
            }catch (e: Exception){
                _toastEvent.emit("Couldn't add your place to favourites ${e.message}")
            }
        }
    }

    fun removeFavouritePlace(cityId: Int){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.removeFavoriteCity(cityId)
            }catch (e: Exception){
                _toastEvent.emit("Couldn't remove your place from favourites ${e.message}")
            }
        }
    }

    fun getFavoriteCity(coord: Coord, isOnline: Boolean = settingRepository.checkNetworkConnection(), lang: String = settingRepository.getSavedLanguage()){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getFavoriteCity(coord, lang)
                .map { data ->
                    data.copy(lastUpdatedDate = getCurrentDateTime(), isFav = true)
                }
                .catch { ex ->
                    _favouriteCity.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }
                .collect { updatedData ->
                    Log.d("date update", "Response: $updatedData")
                    _favouriteCity.value = DataResponse.Success(updatedData)
                    if(isOnline){
                        try {
                            weatherRepository.removeFavoriteCity(updatedData.cityId)
                            addFavouritePlace(cityCurrentWeather = updatedData)
                        } catch (e: Exception) {
                            Log.e("favourite viewmodel", "addCurrentWeather: couldn't add data ${e.message}")
                        }
                    }
                }
        }
    }

    fun getWeatherData(cityId: Int, coord: Coord, isOnline: Boolean = settingRepository.checkNetworkConnection(), lang: String = settingRepository.getSavedLanguage()) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = weatherRepository.getFavoriteCityCurrent(cityId, coord, isOnline, lang)
                .map { data ->
                    data.copy(lastUpdatedDate = getCurrentDateTime(), isFav = true)
                }
                .catch { ex ->
                    _weatherData.value = DataResponse.Failure(ex)
                    _toastEvent.emit("Couldn't fetch data ${ex.message}")
                }

            data.collect { updatedData ->
                Log.d("date update", "Response: $updatedData")
                _weatherData.value = DataResponse.Success(updatedData)
                if(isOnline){
                    try {
                        weatherRepository.removeFavoriteCity(updatedData.cityId)
                        addFavouritePlace(cityCurrentWeather = updatedData)
                    } catch (e: Exception) {
                        Log.e("favourite viewmodel", "addCurrentWeather: couldn't add data ${e.message}")
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getHourlyForecastData(cityId: Int, coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.getFavoriteCityForecast(cityId, coord, isOnline, lang)
                    .catch { ex ->
                        _hourlyForecastData.value = DataResponse.Failure(ex)
                        _toastEvent.emit("Couldn't fetch data: ${ex.message}")
                    }.map { forecastResponse ->
                        if (isOnline) {
                            try {
                                forecastResponse.forEach { forecastEntity -> forecastEntity.isFav = true }
                               addFavouriteForecast(cityForecastWeather = forecastResponse)
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
    private fun getDailyForecastData(cityId: Int, coord: Coord, isOnline: Boolean, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.getFavoriteCityForecast(cityId, coord, isOnline, lang)
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
        cityId: Int,
        lat: Double,
        lon: Double,
        isOnline: Boolean = settingRepository.checkNetworkConnection(),
        lang: String = settingRepository.getSavedLanguage()
    ) {
        getWeatherData(cityId, coord = Coord(lat, lon), isOnline = isOnline, lang = lang)
        getDailyForecastData(cityId, coord = Coord(lat, lon), isOnline = isOnline, lang = lang)
        getHourlyForecastData(cityId, coord = Coord(lat, lon), isOnline = isOnline, lang = lang)
    }


}
class FavouriteFactory(
    private val weatherRepository: WeatherRepository,
    private val settingRepository: SettingRepository,
    ): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(weatherRepository, settingRepository) as T
    }
}