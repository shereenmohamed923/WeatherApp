package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.alert.AlertScreen
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.model.CurrentWeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.LocationRepositoryImpl
import com.example.weatherapp.data.repo.SettingRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.favourite.FavouriteScreen
import com.example.weatherapp.home.HomeFactory
import com.example.weatherapp.home.HomeScreen
import com.example.weatherapp.home.HomeViewModel
import com.example.weatherapp.location.LocationFactory
import com.example.weatherapp.location.LocationViewModel
import com.example.weatherapp.location.MapScreen
import com.example.weatherapp.setting.SettingFactory
import com.example.weatherapp.setting.SettingScreen
import com.example.weatherapp.setting.SettingViewModel
import com.example.weatherapp.utility.DataResponse
import com.example.weatherapp.utility.addressState
import com.example.weatherapp.utility.checkPermissions
import com.example.weatherapp.utility.fusedClient
import com.example.weatherapp.utility.getFreshLocation
import com.google.android.gms.location.LocationServices

const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : ComponentActivity() {

    val settingsViewModel by lazy {
        ViewModelProvider(
            this, SettingFactory(
                SettingRepositoryImpl.getInstance(this),
                LocationRepositoryImpl.getInstance(this)
            )
        ).get(SettingViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addressState = mutableStateOf("")
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

//        val languageCode = settingsViewModel.getSavedLanguage()
//        settingsViewModel.saveLanguage(languageCode)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Scaffold(
                bottomBar = {
                    if (currentRoute != "location") { // Hide bottom bar on MapScreen
                        BottomNavigationBar(navController = navController)
                    }
                },
                content = { padding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.app_background),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xCC352163),
                                            Color(0xCC331972),
                                            Color(0xCC33143C)
                                        )
                                    )
                                )
                        )
                        NavHostContainer(navController = navController, padding = padding)
                    }
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostContainer(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.padding(paddingValues = padding),

        builder = {
            composable("home") {
                HomeScreen()
            }
            composable("favourite") {
                FavouriteScreen()
            }
            composable("alert") {
                AlertScreen()
            }
            composable("setting") {
                SettingScreen(navController)
            }
            composable("location") {
                MapScreen(navController)
            }
        })
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {

    NavigationBar(
        containerColor = Color.Transparent,
        modifier = Modifier.padding(8.dp)
    ) {

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Constants.BottomNavItems.forEach { navItem ->
            val isSelected = currentDestination?.route == navItem.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(navItem.route)
                },
                icon = {
                    Icon(imageVector = navItem.icon, "")
                },
                alwaysShowLabel = false,
                modifier = if (isSelected) {
                    Modifier
                        .size(20.dp, 40.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xAA957DCD),
                                    Color(0xAA523D7F)
                                )
                            ),
                            shape = RoundedCornerShape(40.dp)
                        )
                } else Modifier,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.Transparent

                )
            )
        }
    }
}

