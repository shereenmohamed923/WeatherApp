package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.alert.AlertScreen
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.SettingRepositoryImpl
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.favourite.FavoriteDetailsScreen
import com.example.weatherapp.favourite.FavouriteFactory
import com.example.weatherapp.favourite.FavouriteScreen
import com.example.weatherapp.favourite.FavouriteViewModel
import com.example.weatherapp.home.HomeScreen
import com.example.weatherapp.location.MapScreen
import com.example.weatherapp.setting.SettingScreen
import com.example.weatherapp.utility.fusedClient
import com.google.android.gms.location.LocationServices

const val My_LOCATION_PERMISSION_ID = 5005

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val favouriteViewModel: FavouriteViewModel = ViewModelProvider(this,
            factory = FavouriteFactory(
                WeatherRepositoryImpl.getInstance(
                    RemoteDataSourceImpl(RetrofitHelper.service), LocalDataSourceImpl(
                        WeatherDatabase.getInstance(this).weatherDao())
                ),
                SettingRepositoryImpl.getInstance(this)
            )
        ).get(FavouriteViewModel::class.java)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Scaffold(
                bottomBar = {
                    if (currentRoute != "location") {
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
                        NavHostContainer(navController = navController, padding = padding, favouriteViewModel)
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
    padding: PaddingValues,
    favouriteViewModel: FavouriteViewModel
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
                FavouriteScreen(navController, favouriteViewModel)
            }
            composable("alert") {
                AlertScreen()
            }
            composable("setting") {
                SettingScreen(navController)
            }
            composable("location/{source}") {backStackEntry ->
                val source = backStackEntry.arguments?.getString("source") ?: ""
                MapScreen(navController, source)
            }
            composable("favoriteDetails") {
                FavoriteDetailsScreen(favouriteViewModel)
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

