// File: com/example/wellipet/navigation/AppNavHost.kt
package com.example.wellipet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.compose.ui.platform.LocalContext
import com.example.wellipet.ui.auth.LoginScreen
import com.example.wellipet.ui.auth.SignUpScreen
import com.example.wellipet.ui.mobile.home.HomeScreen
import com.example.wellipet.ui.mobile.healthdata.HealthDataScreen
import com.example.wellipet.ui.mobile.store.StoreScreen
import com.google.firebase.auth.FirebaseAuth
import com.example.wellipet.data.AuthPreferences.rememberMeFlow
import com.example.wellipet.data.AuthPreferences.setRememberMe



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
    val context = LocalContext.current
    val rememberMe by context.rememberMeFlow.collectAsState(initial = false)
    val firebaseUser = FirebaseAuth.getInstance().currentUser


    val start = if (firebaseUser != null && rememberMe) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }


    NavHost(navController = navController, startDestination = start) {
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
                    navController.navigate(Screen.Login.route) {
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

