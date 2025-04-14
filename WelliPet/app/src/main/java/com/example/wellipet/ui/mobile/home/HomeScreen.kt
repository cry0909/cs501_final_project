// File: com/example/wellipet/ui/mobile/home/HomeScreen.kt
package com.example.wellipet.ui.mobile.home

import RequestLocationPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import com.example.wellipet.R
import com.example.wellipet.utils.getSuggestionText
import com.example.wellipet.utils.getWeatherIconRes
import com.example.wellipet.ui.components.BottomNavigationBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = viewModel()
) {
    // 請求位置權限，這裡僅作為一開始確認權限已授予
    RequestLocationPermission { granted ->
        // 由 HomeViewModel 自動訂閱位置變化
    }

    val weatherResponse by homeViewModel.weatherResponse.collectAsState()

    // 初始化 GIF 用的 ImageLoader
    val context = LocalContext.current
    val gifImageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(ImageDecoderDecoder.Factory()) }
            .build()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("WelliPet - Home") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 背景圖片
            AsyncImage(
                model = R.drawable.bg_park,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            // 顯示天氣資訊區塊
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                if (weatherResponse != null) {
                    Text(
                        text = "City: ${weatherResponse!!.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Text(
                        text = "Temp: ${weatherResponse!!.main.temp}°C",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    weatherResponse!!.weather.forEach { weather ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            val iconRes = getWeatherIconRes(weather.description)
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = weather.description,
                                modifier = Modifier.size(48.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = weather.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                        val suggestion = getSuggestionText(weather.description)
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "Loading weather...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                }
            }
            // 中央顯示寵物內容
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = R.drawable.dog,
                    imageLoader = gifImageLoader,
                    contentDescription = "Animated Pet",
                    modifier = Modifier
                        .size(300.dp)
                        .clip(CircleShape)
                )
                Text("Feeling Happy", style = MaterialTheme.typography.headlineSmall, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daily Health progress: Steps / Sleep / Water ...", color = Color.White)
            }
        }
    }
}
