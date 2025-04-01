package com.example.wellipet.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.wellipet.navigation.Screen


@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.HealthData,
        Screen.Store
    )
    NavigationBar {
        val currentDestination = navController.currentBackStackEntryAsState().value?.destination
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Home -> Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home"
                        )
                        Screen.HealthData -> Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Health Data"
                        )
                        Screen.Store -> Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Store"
                        )
                        else -> Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Default"
                        )
                    }
                },
                label = { Text(screen.label) },
                selected = currentDestination?.route == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier,
                enabled = true,
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(),
                interactionSource = remember { MutableInteractionSource() }
            )
        }
    }
}
