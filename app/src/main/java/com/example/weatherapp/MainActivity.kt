package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.model.Coordinate
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.home.HomeViewModel
import com.example.weatherapp.utility.DataResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repo = WeatherRepositoryImpl.getInstance(
            RemoteDataSourceImpl(RetrofitHelper.service)
        )
        val viewModel: HomeViewModel = HomeViewModel(repo)

        setContent {
            viewModel.getWeatherData(Coordinate(30.6118656, 32.2895872), true)
            val dataState = viewModel.weatherData.collectAsState()
                when (val response = dataState.value) {
                    is DataResponse.Loading -> {
                        Log.i("TAG", "current weather loading: ")
                    }

                    is DataResponse.Success -> {
                        Log.i("TAG", "current weather success: ${response.data}")
                    }

                    is DataResponse.Failure -> {
                        Log.d("TAG", "current weather error: ${response.error}")
                    }
                }

            viewModel.getForecastData(Coordinate(30.6118656, 32.2895872), true)
            val forecastState = viewModel.forecastData.collectAsState()
            when (val response = forecastState.value) {
                is DataResponse.Loading -> {
                    Log.i("TAG", "forecast loading: ")
                }

                is DataResponse.Success -> {
                    Log.i("TAG", "forecast succeeded: ${response.data}")
                }

                is DataResponse.Failure -> {
                    Log.d("TAG", "forecast error: ${response.error}")
                }
            }

            }
        }

    }
