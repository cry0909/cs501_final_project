package com.example.wellipet.presentation

import android.os.Bundle
import android.util.Log
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
        // 1) 手動註冊 listener
        authListener = AuthMessageListener(this)
        Wearable.getMessageClient(this)
            .addListener(authListener)
            .addOnSuccessListener { android.util.Log.d("WatchAuth","Listener registered") }
            .addOnFailureListener { android.util.Log.e("WatchAuth","register failed", it) }


        setContent {
            WelliPetTheme {
                val navController = rememberSwipeDismissableNavController()
                val authVm: WatchAuthViewModel = viewModel()
//                 根据是否拿到 token 决定起始页
                val uid by authVm.uidFlow.collectAsState()
                val start = if (uid != null) "home" else "auth"
//                val start = "home"

                SwipeDismissableNavHost(navController, startDestination = start) {
                    composable("auth") {
                        // 登入前：显示等待同步
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "等待手機登入並同步…",
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
        // 2) 在 Activity 销毁时注销 listener
        Wearable.getMessageClient(this)
            .removeListener(authListener)
    }
}

