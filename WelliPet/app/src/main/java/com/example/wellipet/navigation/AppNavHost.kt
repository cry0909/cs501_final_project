// File: com/example/wellipet/navigation/AppNavHost.kt
package com.example.wellipet.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.wellipet.ui.auth.LoginScreen
import com.example.wellipet.ui.auth.SignUpScreen
import com.example.wellipet.ui.mobile.home.HomeScreen
import com.example.wellipet.ui.mobile.healthdata.HealthDataScreen
import com.example.wellipet.ui.mobile.store.StoreScreen

sealed class Screen(val route: String, val label: String) {
    object Login : Screen("login", "Login")
    object SignUp : Screen("signup", "Sign Up")
    object Home : Screen("home", "Home")
    object HealthData : Screen("healthData", "Health Data")
    object Store : Screen("store", "Store")
}




@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.HealthData.route) {
            HealthDataScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Screen.Store.route) {
            StoreScreen(onBackClick = { navController.popBackStack() })
        }
    }
}

