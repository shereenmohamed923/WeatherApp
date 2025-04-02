package com.example.weatherapp.favourite


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.weatherapp.R
import com.example.weatherapp.data.local.LocalDataSourceImpl
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.entities.CurrentWeatherEntity
import com.example.weatherapp.data.model.Coord
import com.example.weatherapp.data.remote.RemoteDataSourceImpl
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.utility.DataResponse

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavouriteScreen(navController: NavController, favouriteViewModel: FavouriteViewModel) {

    favouriteViewModel.getAllFavouritePlaces()

    val favouritePlacesState = favouriteViewModel.favouritePlaces.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("location/favorites") },
                containerColor = Color(0xFF523D7F),
                contentColor = Color.White
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_mylocation),
                    contentDescription = "Add location"
                )
            }
        },
        content = {
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
                {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        when(val state = favouritePlacesState.value){
                            is DataResponse.Loading -> {
                                CircularProgressIndicator()
                            }

                            is DataResponse.Success -> {

                                if(state.data is List<*>){
                                    if(state.data.isEmpty()){

                                    }else{
                                        val favouritePlaces = state.data as List<CurrentWeatherEntity>
                                        FavouritePlaces(
                                            places = favouritePlaces,
                                            viewModel = favouriteViewModel,
                                            navController
                                        )
                                        Log.i("favourite", "favouritePlaces: $favouritePlaces")
                                    }
                                    Log.i("favourite", "FavouriteScreen: successful")
                                }
                            }

                            is DataResponse.Failure -> {
                                Log.i("favourite", "favouritePlaces: ${state.error}" )
                            }
                        }
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavouritePlaces(places: List<CurrentWeatherEntity>, viewModel: FavouriteViewModel, navController: NavController){
    Column {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(places){ place ->
                WeatherCard(
                    city = place.cityName,
                    condition = place.weatherDescription,
                    iconRes = R.drawable.cloudy_weather,
                    cityId = place.cityId,
                    onClick = {
                        viewModel.getWeatherData(Coord(place.lat, place.lon), true, "en")
                        viewModel.getDailyForecastData(Coord(place.lat, place.lon), true, "en")
                        viewModel.getHourlyForecastData(Coord(place.lat, place.lon), true, "en")
                        navController.navigate("favoriteDetails")
                    }
                ){
                    viewModel.removeFavouritePlace(place.cityId)
                }
            }
        }
    }

}

@Composable
fun WeatherCard(city: String, condition: String, iconRes: Int, cityId: Int, onClick: () -> Unit,  onDelete: (cityId: Int) -> Unit ={}) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF957DCD),
                            Color(0xFF523D7F)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = city, fontSize = 18.sp, color = Color.White)
                    Text(text = condition, fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f))
                }
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "",
                    modifier = Modifier
                        .size(80.dp),
                    contentScale = ContentScale.Fit
                )
                Button(
                    onClick = { onDelete(cityId) }
                ) { Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "",
                    tint = Color.Red
                ) }
            }
        }

    }
}
