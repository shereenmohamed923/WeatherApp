package com.example.weatherapp.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.LocationRepositoryImpl
import com.example.weatherapp.data.repo.SettingRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.home.formatNumber
import com.example.weatherapp.utility.NetworkUtils
import com.example.weatherapp.utility.UnitHelper
import com.example.weatherapp.utility.WeatherIcon
import kotlinx.coroutines.flow.catch

class WeatherAlertWorker(
    private val context: Context,
    params: WorkerParameters
)  : CoroutineWorker(context, params){

    private val repository =
        WeatherRepositoryImpl.getInstance(
            RemoteDataSourceImpl(RetrofitHelper.service), LocalDataSourceImpl(
                WeatherDatabase.getInstance(context).weatherDao())
        )
    private val settingRepository = SettingRepositoryImpl.getInstance(context)

    override suspend fun doWork(): Result {
        val alertId = inputData.getLong("alert_id", -1)
        if (alertId == -1L) return Result.failure()

        val alert = repository.getAlertById(alertId)

        if(NetworkUtils.isNetworkAvailable(applicationContext)){
            val weatherResult = repository.getCurrentWeather(Coord(alert.latitude, alert.longitude), true, settingRepository.getSavedLanguage())
            weatherResult
                .catch {
                    Log.i("worker", "doWork: ${it.message}")
                    return@catch
                }
                .collect{ currentWeather ->
                    showNotification(
                        currentWeather.weatherDescription,
                        WeatherIcon(currentWeather.weatherIcon),
                        currentWeather.cityName
                    )
            }
        } else {
            showNotification(
                context.getString(R.string.no_network_connection),
                R.drawable.wind,
                context.getString(R.string.app_name)
            )
        }

        repository.editAlert(alert.copy(isNotified = true))
        return Result.success()
    }

    private fun showNotification(message: String, iconRes: Int, title: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_channel",
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "weather_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(iconRes)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, iconRes))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
