package com.example.weatherapp.alert

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.weatherapp.data.local.entities.WeatherAlertsEntity
import com.example.weatherapp.data.repo.LocationRepository
import com.example.weatherapp.data.repo.SettingRepository
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.data.worker.WeatherAlertWorker
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.TimeUnit

class AlertViewModel(private val weatherRepository: WeatherRepository,private val locationRepository: LocationRepository) : ViewModel() {

    private val _allAlerts = MutableStateFlow<DataResponse>(DataResponse.Loading)
    val allAlerts = _allAlerts.asStateFlow()

    fun getAllAlerts(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getAllAlerts()
                .catch { ex ->
                    _allAlerts.value = DataResponse.Failure(ex)
                }
                .collect { alerts ->
                    Log.d("all Alerts", "Response: $alerts")
                    alerts.forEach { alert->
                        if(alert.isNotified){
                            deleteAlert(context, alert)
                        }
                    }
                    _allAlerts.value = DataResponse.Success(alerts)
                }
        }
    }

    fun scheduleWeatherAlert(context: Context, scheduledTime: Long) {
        viewModelScope.launch {
            val savedLocation = locationRepository.getSavedLocation()
            val locationName = locationRepository.getSavedLocationName()
            val alert = WeatherAlertsEntity(
                locationName = locationName,
                latitude = savedLocation.lat,
                longitude = savedLocation.lon,
                scheduledTime = scheduledTime
            )
            val alertId = weatherRepository.addAlert(alert)
            scheduleWorker(context, alertId, scheduledTime)
        }
    }

    fun deleteAlert(context: Context, alert: WeatherAlertsEntity) {
        viewModelScope.launch {
            weatherRepository.removeAlert(alert)
            WorkManager.getInstance(context).cancelWorkById(
                UUID.nameUUIDFromBytes("weather_alert_${alert.id}".toByteArray())
            )
        }
    }

    private fun scheduleWorker(context: Context, alertId: Long, scheduledTime: Long) {
        val currentTime = System.currentTimeMillis()
        val delayInMillis = scheduledTime - currentTime

        if (delayInMillis <= 0) return

        val inputData = workDataOf("alert_id" to alertId)

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setId(UUID.nameUUIDFromBytes("weather_alert_$alertId".toByteArray()))
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                100L,
                TimeUnit.MILLISECONDS
            )
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

class AlertFactory(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(weatherRepository, locationRepository) as T
    }
}