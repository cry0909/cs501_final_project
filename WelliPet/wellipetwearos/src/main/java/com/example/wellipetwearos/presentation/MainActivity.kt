package com.example.wellipetwearos.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wellipetwearos.presentation.theme.WelliPetTheme
import com.example.wellipetwearos.presentation.ui.wear.WearHealthDataScreen
import com.example.wellipetwearos.presentation.ui.wear.WearHomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 安裝 SplashScreen（如果需要）
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)
        setContent {
            WelliPetTheme {
                WearHomeScreen()//WearHealthDataScreen()
            }
        }
    }
}

