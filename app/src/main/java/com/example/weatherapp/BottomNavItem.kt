package com.example.weatherapp

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val icon: ImageVector,
    val route:String,
)

object Constants{
    val BottomNavItems = listOf(
        BottomNavItem(
            icon = Icons.Filled.Home,
            route = "home"
        ),
        BottomNavItem(
            icon = Icons.Filled.Favorite,
            route = "favourite"
        ),
        BottomNavItem(
            icon = Icons.Filled.Notifications,
            route = "alert"
        ),
        BottomNavItem(
            icon = Icons.Filled.Settings,
            route = "setting"
        )
    )
}