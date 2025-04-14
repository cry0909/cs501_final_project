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
import com.example.wellipet.api.WeatherViewModel
import com.example.wellipet.ui.components.BottomNavigationBar

import com.example.wellipet.getSuggestionText
import com.example.wellipet.getWeatherIconRes
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    weatherViewModel: WeatherViewModel = viewModel()
) {
    // 请求位置权限，若授权则调用 fetchWeatherByLocation，否则查询默认城市（例如 "Beijing"）
    RequestLocationPermission { granted ->
        if (granted) {
            weatherViewModel.fetchWeatherByLocation()
        }
    }

    // 观察天气数据状态
    val weatherResponse by weatherViewModel.weatherState.collectAsState()

    // 初始化用于加载 GIF 图片的 ImageLoader
    val context = LocalContext.current
    val gifImageLoader = remember {
        ImageLoader.Builder(context)
            .components { add(ImageDecoderDecoder.Factory()) }
            .build()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("WelliPet - Home") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 背景图片
            AsyncImage(
                model = R.drawable.bg_park,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            // 左上角显示天气信息（图标 + 文字描述 + 运动建议）
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                if (weatherResponse != null) {
                    Text(
                        text = "City：${weatherResponse!!.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Temp：${weatherResponse!!.main.temp}°C",
                        style = MaterialTheme.typography.bodySmall
                    )
                    // 遍历所有天气情况
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
                                tint = Color.Unspecified // 取消默认着色，显示原始颜色
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = weather.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        // 显示对应的运动建议
                        val suggestion = getSuggestionText(weather.description)
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            // 居中显示宠物内容
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
                Text("Feeling Happy", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daily Health progress: Steps / Sleep / Water ...")
            }
        }
    }
}