package com.example.weatherapp.favourite


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
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
import com.example.weatherapp.utility.WeatherIcon

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavouriteScreen(navController: NavController, favouriteViewModel: FavouriteViewModel) {

    favouriteViewModel.getAllFavouritePlaces()

    val favouritePlacesState = favouriteViewModel.favouritePlaces.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("location/favorites") },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
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
                            .padding(16.dp)
//                            .scrollable()
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
                    iconRes = WeatherIcon(place.weatherIcon),
                    cityId = place.cityId,
                    onClick = {
                        viewModel.refreshWeatherData(place.cityId, place.lat, place.lon)
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp),
                    contentScale = ContentScale.Fit
                )
                Column {
                    Text(
                        text = city,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = condition,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.fillMaxWidth(fraction = 0.8f))
                IconButton(onClick = { onDelete(cityId) }) {
                    Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete favourite",
                    tint = MaterialTheme.colorScheme.error
                ) }
            }

    }
}
