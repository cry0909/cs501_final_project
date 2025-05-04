package com.example.wellipet.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.*
import com.example.wellipet.presentation.data.AuthMessageListener
import com.example.wellipet.presentation.ui.wear.*
import com.example.wellipet.presentation.theme.WelliPetTheme
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {
    private lateinit var authListener: AuthMessageListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1) Manually register the message listener for auth data
        authListener = AuthMessageListener(this)
        Wearable.getMessageClient(this)
            .addListener(authListener)


        setContent {
            WelliPetTheme {
                val navController = rememberSwipeDismissableNavController()
                val authVm: WatchAuthViewModel = viewModel()
                // Choose start destination based on whether a UID has been received
                val uid by authVm.uidFlow.collectAsState()
                val start = if (uid != null) "home" else "auth"

                SwipeDismissableNavHost(navController, startDestination = start) {
                    composable("auth") {
                        // Before login: display waiting for phone sync
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Waiting for sync from phone…",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary

                            )
                        }
                    }
                    composable("home") {
                        WatchHomeScreen()
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // 2) Unregister the listener when the Activity is destroyed
        Wearable.getMessageClient(this)
            .removeListener(authListener)
    }
}

