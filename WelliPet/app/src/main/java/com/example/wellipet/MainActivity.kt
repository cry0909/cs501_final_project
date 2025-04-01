// File: com/example/wellipet/MainActivity.kt
package com.example.wellipet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.wellipet.navigation.AppNavHost
import com.example.wellipet.ui.theme.WelliPetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WelliPetTheme {
                AppNavHost()
            }
        }
    }
}
